# meetime-hubspot-integration

Esta aplicação implementa o fluxo de autorização OAuth 2.0 do HubSpot usando Java e Spring Boot. Ela expõe dois
endpoints para gerar a URL de autorização e trocar o código de autorização por um token de acesso.

---

## 📋 Pré-requisitos

- Java 17+
- Maven 3.6+
- Conta de desenvolvedor no [HubSpot](https://developers.hubspot.com/) (para obter `client.id` e `client.secret`).
- Lombok para esconder códigos boilerplate e manter o código mais limpo

---

## ⚙️ Configuração

### Variáveis de Ambiente

A aplicação utiliza as seguintes variáveis de ambiente para autenticação:

| Variável        | Descrição                              | Exemplo                                |
|-----------------|----------------------------------------|----------------------------------------|
| `client.id`     | ID do cliente do aplicativo no HubSpot | `3eebdb05-ede1-49c2-af8b-925d6cc8a1f1` |
| `client.secret` | Segredo do cliente do aplicativo       | `c164fc2d-0237-46c6-85cd-46a876cf14da` |

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

# 📋 Fluxo de criação de contato

### Criação de contatos no HubSpot CRM

## 📥 Requisição

### Corpo da Requisição (JSON)

| Campo       | Tipo   | Descrição                     | Obrigatório |
|-------------|--------|-------------------------------|-------------|
| `email`     | String | E-mail do contato             | Sim         |
| `firstName` | String | Primeiro nome do contato      | Sim         |
| `lastName`  | String | Sobrenome do contato          | Sim         |
| `phone`     | String | Número de telefone do contato | Não         |

Exemplo de corpo de requisição:

```json
{
    "email": "pedro@meetime.com",
    "lastname": "lacerda",
    "firstname": "pedro"
}
```

## 🔍 Testando o fluxo de criação de contato

- Acesse http://localhost:8080/meetime-hubspot/create/contact para obter a URL de autorização.

- Envie o corpo de requisição.

- Insira o token de acesso recuperado no passo anterior (endpoint que de autorização /meetime-hubspot/auth) no HEADER "
  accessToken".

- Caso retorne sucesso (201) a aplicação retornará o seguinte corpo:

```json lines
{
  "createdAt":"2025-03-13T23:04:46.122Z",
  "archived":false,
  "archivedAt":null,
  "propertiesWithHistory":null,"id":"105890692533",
  "properties": {
    "createdate":"2025-03-13T23:04:46.122Z",
    "hs_all_contact_vids":"105890692533",
    "hs_associated_target_accounts":"0","hs_currently_enrolled_in_prospecting_agent":"false",
    "hs_is_contact":"true","hs_is_unworked":"true",
    "hs_lifecyclestage_lead_date":"2025-03-13T23:04:46.122Z",
    "hs_membership_has_accessed_private_content":"0",
    "hs_object_id":"105890692533","hs_object_source":"INTEGRATION",
    "hs_object_source_id":"9280840",
    "hs_object_source_label":"INTEGRATION",
    "hs_pipeline":"contacts-lifecycle-pipeline","hs_prospecting_agent_actively_enrolled_count":"0",
    "hs_registered_member":"0",
    "hs_sequences_actively_enrolled_count":"0",
    "lastmodifieddate":"2025-03-13T23:04:46.122Z",
    "lifecyclestage":"lead",
    "num_notes":"0"},
  "updatedAt":"2025-03-13T23:04:46.122Z"
}
```
































