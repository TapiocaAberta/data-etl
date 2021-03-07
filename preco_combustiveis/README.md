Preço dos combustívels no Brasil
--

O objetivo desse processo de ETL é agrupar os dados de ETL do Brasil para ter uma média do valor por ano em cada estado.

*O resultado do processamento não foi ainda verificado. Se achar alguma disparidade contribua abrindo uma issue*

### Executando

Para executar utilize a ferramenta jbang com Java na versão 15. Primeiro você precisa baixar alguns arquivos:

```
jbang SummarizeFuelPrice.java DOWNLOAD
```
Com arquivos baixados, você precisará converter o encoding para UTF-8. No Linux o script na pasta "downloaded" irá ajudar. Após converter copie os que quer processar do diretório `downloaded` para `input` e rode:
```
jbang SummarizeFuelPrice.java PROCESS
```
Isso vai substituir o arquivo `processed.csv` com o resultado.
