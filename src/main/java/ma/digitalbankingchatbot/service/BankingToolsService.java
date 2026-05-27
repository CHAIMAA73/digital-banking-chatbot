package ma.digitalbankingchatbot.service;

import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class BankingToolsService {

    @Value("${banking.api.url}")
    private String apiUrl;

    @Value("${banking.api.token}")
    private String apiToken;

    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Tool("Rechercher un client par son nom ou prénom")
    public String searchCustomer(String name) {
        try {
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl + "/customers/search?keyword=" + name,
                    HttpMethod.GET, entity, String.class);
            return "Clients trouvés : " + response.getBody();
        } catch (Exception e) {
            log.error("Erreur recherche client: {}", e.getMessage());
            return "Impossible de récupérer les informations client.";
        }
    }

    @Tool("Obtenir les informations d'un compte bancaire par son ID")
    public String getAccountInfo(String accountId) {
        try {
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl + "/accounts/" + accountId,
                    HttpMethod.GET, entity, String.class);
            return "Informations du compte : " + response.getBody();
        } catch (Exception e) {
            log.error("Erreur récupération compte: {}", e.getMessage());
            return "Compte introuvable ou erreur d'accès.";
        }
    }

    @Tool("Obtenir l'historique des opérations d'un compte bancaire")
    public String getAccountHistory(String accountId) {
        try {
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl + "/accounts/" + accountId + "/operations",
                    HttpMethod.GET, entity, String.class);
            return "Historique des opérations : " + response.getBody();
        } catch (Exception e) {
            log.error("Erreur récupération historique: {}", e.getMessage());
            return "Impossible de récupérer l'historique.";
        }
    }

    @Tool("Obtenir la liste de tous les clients")
    public String getAllCustomers() {
        try {
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl + "/customers",
                    HttpMethod.GET, entity, String.class);
            return "Liste des clients : " + response.getBody();
        } catch (Exception e) {
            log.error("Erreur liste clients: {}", e.getMessage());
            return "Impossible de récupérer la liste des clients.";
        }
    }
}