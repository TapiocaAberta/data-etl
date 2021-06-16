Agenda do Presidente
--

Dados coletados do site [Agenda do Presidente](https://www.gov.br/planalto/pt-br/acompanhe-o-planalto/agenda-do-presidente-da-republica/). Os dados estão no formato CSV, separados por presidente, ano,e mês. Cada arquivo representa um dia da agenda do Presidente. Os dados coletados são a partir de 01/01/2019. Os arquivos contem os seguintes campos:

* DIA: Dia da agenda
* SEM_COMPROMISSO: Campo _boolean_ (Sim ou Não) que representa se existe ou não compromissos para aquele dia.
* DURACAO_DIA: Soma do tempo de todos os compromissos daquele dia. Campos em minutos
* INICIO: Hora de inicio de um compromisso em horas (HH:mm)
* FIM: Hora do fim de um compromisso, em horas (HH:mm)
* DURACAO_COMPRIMISSO: Duranção daquele compromisso em minutos
* TITULO: Título de um compromisso
* LOCAL: Local em que aquele compromisso aconteceu.

Qualquer sugestão deixar em issues. O projeto principal para extração dos dados está em: https://github.com/pedro-hos/agenda-presidentes

