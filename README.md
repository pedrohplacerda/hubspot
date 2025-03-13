# meetime-hubspot-integration

Esta aplicação implementa o fluxo de autorização OAuth 2.0 do HubSpot usando Java e Spring Boot. Ela expõe dois endpoints para gerar a URL de autorização e trocar o código de autorização por um token de acesso.

---

## 📋 Pré-requisitos

- Java 11+
- Maven 3.6+
- Conta de desenvolvedor no [HubSpot](https://developers.hubspot.com/) (para obter `client.id` e `client.secret`).

---

## ⚙️ Configuração

### Variáveis de Ambiente
A aplicação utiliza as seguintes variáveis de ambiente para autenticação:

| Variável          | Descrição                               | Exemplo                                   |
|-------------------|-----------------------------------------|-------------------------------------------|
| `client.id`       | ID do cliente do aplicativo no HubSpot  | `3eebdb05-ede1-49c2-af8b-925d6cc8a1f1`    |
| `client.secret`   | Segredo do cliente do aplicativo        | `c164fc2d-0237-46c6-85cd-46a876cf14da`    |

#### Definindo as variáveis:
Você pode passar as variáveis via linha de comando ao executar a aplicação:
```bash
mvn spring-boot:run -Dclient.id=SEU_CLIENT_ID -Dclient.secret=SEU_CLIENT_SECRET
```
Também pode adicionar como VM Options do intelliJ.

 ## 🔍 Testando o fluxo de autorização

- Acesse http://localhost:8080/meetime-hubspot/auth para obter a URL de autorização.

- Siga a URL gerada e autorize o aplicativo no HubSpot.

- Após autorizar, você será redirecionado para http://localhost:8080/meetime-hubspot/callback?code=SEU_CODIGO.

- A aplicação trocará o código por um token de acesso e exibirá a resposta.