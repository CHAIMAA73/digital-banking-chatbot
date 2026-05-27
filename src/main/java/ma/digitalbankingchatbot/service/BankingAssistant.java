package ma.digitalbankingchatbot.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface BankingAssistant {

    @SystemMessage("""
        Tu es un assistant bancaire intelligent pour Digital Banking.
        Tu aides les clients avec leurs questions sur :
        - Leurs comptes bancaires (courants et épargne)
        - Leurs opérations (débits, crédits)
        - Les informations générales sur la banque
        
        Réponds toujours en français de manière professionnelle et concise.
        Si tu ne sais pas quelque chose, dis-le honnêtement.
        Ne divulgue jamais d'informations confidentielles d'autres clients.
    """)
    String chat(@UserMessage String userMessage);
}