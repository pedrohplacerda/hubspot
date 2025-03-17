# ❇️ meetime-hubspot-integration

Esta aplicação faz integração com apis do Hubspot para:

- criar uma url de autenticação parar seguir com o *authorization code flow*
- criar um callback parar finalizar o *authorization code flow* e trocar por um token de aceso
- criar um contato utilizando api **/crm/v3/objects/contacts/{contactId}** e o token de acesso obtido através do *authorization code flow*
- escutar o webhook de criação de contato a partir da requisição feita para a api do Hubspot

---

## 📋 Pré-requisitos

- Java 17+
- Maven 3.6+
- Conta de desenvolvedor no [HubSpot](https://developers.hubspot.com/) (para obter `client.id` e `client.secret`).
- Lombok para esconder códigos boilerplate e manter o código mais limpo
- [ngrok](https://dashboard.ngrok.com/get-started/setup/windows) para expor o servidor local (http://localhost:8080) em um domínio https para testar os webhooks

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

---

## 🔍 Testando o fluxo de autorização

- Acesse http://localhost:8080/meetime-hubspot/auth para obter a URL de autorização.

- Siga a URL gerada e autorize o aplicativo no HubSpot.

- Após autorizar, você será redirecionado para http://localhost:8080/meetime-hubspot/callback?code=SEU_CODIGO.

- A aplicação trocará o código por um token de acesso e exibirá a resposta.

---

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

---

# 🪀 Fluxo que escuta os webhooks de criação de contato do hubspot

## Configurações para testar localmente

- Adicionar as variáveis de ambiente no VM Options

```bash
-Dhubspot.auth.url=https://app.hubspot.com/oauth/authorize?client_id=3eebdb05-ede1-49c2-af8b-925d6cc8a1f1&redirect_uri=http://localhost:8080/meetime-hubspot/callback&scope=crm.objects.contacts.write%20oauth%20crm.objects.companies.write%20crm.objects.companies.read%20crm.objects.contacts.read
-Dclient.id=3eebdb05-ede1-49c2-af8b-925d6cc8a1f1
-Dclient.secret=c164fc2d-0237-46c6-85cd-46a876cf14da
-Dhubspot.redirect.uri=http://localhost:8080/meetime-hubspot/callback
-Dhubspot.api.url=https://api.hubapi.com
```
- Executar o projeto localmente;
- Executar o ngrok conforme o link acima para expor o servidor local num servidor efêmero https;
- Configurar o webhook a ser enviado no [portal de aplicativos](https://app.hubspot.com/developer/49524927/application/9280840/webhooks1) da conta de developer. Indicar o servidor criado pelo ngrok;
- Fazer uma chamada ao endpoint *GET meetime-hubspot/auth* para receber a url de autorização;
- Copiar e colar a url de autorização no browser. Certifique-se que a conta hubspot logada ***não*** seja de desenvolvedor;
- Parar a execução do aplicativo;
- Copiar e colar o token retornado na tela do browser em dois lugares: 1 - header para a chamada ao endpoint de criação de contato. 2 - Na linha 138 da classe [HubspotOutputAdapter](src/main/java/com/meetime/hubspot/infrastructure/http/adapter/HubspotOutputPortAdapter.java) ***(solução temporária até implementar a lógica de callback para recuperar o token criado)***;
- Executar o aplicativo localmente novamente;
- Faça uma chamada ao endpoint *POST meetime-hubspot/create/contact* parar criar um contato;
- O webhook será recebido e processado corretamente;

---

# 🚀 Melhorias

- Implementar fluxo que recupera o token de acesso para o processamento correto do webhook de criação de contato, buscando na api do hubspot as informações do contato criado para salvar no banco de dados localmente;
- Criação de testes unitários, de integração e de mutação para garantir robustez e resiliência do código;
- Modelagem do banco de dados para salvar as informações do contato criado para ser disponibilizado em outros fluxos da meetime;
- Containerização da aplicação para facilitar o deploy em máquinas virtuais dentro do ambiente de cloud (EC2 da AWS e GCE do GCP);
- Melhorar o fluxo de criação da url de autorização para receber de maneira dinâmica o client.secret e client.id parar poder criar urls parar qualquer usuário que requisitar;































