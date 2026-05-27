package ma.digitalbankingchatbot.telegram;

import lombok.extern.slf4j.Slf4j;
import ma.digitalbankingchatbot.service.BankingAssistant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot {

    private final BankingAssistant bankingAssistant;

    @Value("${telegram.bot.username}")
    private String botUsername;

    public TelegramBotService(
            @Value("${telegram.bot.token}") String botToken,
            BankingAssistant bankingAssistant) {
        super(botToken);
        this.bankingAssistant = bankingAssistant;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userMessage = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getFirstName();

            log.info("📩 Message de {} : {}", userName, userMessage);

            // Commande /start
            if (userMessage.equals("/start")) {
                String welcomeMessage = "👋 Bonjour " + userName + " !\n" +
                        "Je suis votre assistant bancaire Digital Banking.\n\n" +
                        "Je peux vous aider avec :\n" +
                        "• 🏦 Informations sur vos comptes\n" +
                        "• 📊 Historique de vos opérations\n" +
                        "• ❓ Questions générales sur nos services\n\n" +
                        "Comment puis-je vous aider aujourd'hui ?";
                sendMessage(chatId, welcomeMessage);
                return;
            }

            // Commande /help
            if (userMessage.equals("/help")) {
                sendMessage(chatId, """
                    📖 Aide - Digital Banking Bot
                    
                    Exemples de questions :
                    • Quel est le solde du compte ACC123 ?
                    • Montre-moi les opérations du compte ACC456
                    • Cherche le client Ahmed
                    • Qu'est-ce qu'un compte épargne ?
                    """);
                return;
            }

            // Réponse AI via RAG
            try {
                sendMessage(chatId, "⏳ Je traite votre demande...");
                String response = bankingAssistant.chat(userMessage);
                sendMessage(chatId, response);
                log.info("✅ Réponse envoyée à {}", userName);
            } catch (Exception e) {
                log.error("❌ Erreur: {}", e.getMessage());
                sendMessage(chatId, "❌ Désolé, une erreur s'est produite. Réessayez.");
            }
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("❌ Erreur envoi Telegram: {}", e.getMessage());
        }
    }
}