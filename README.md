Este projeto implementa um servidor de perguntas e respostas em Java, utilizando sockets TCP para comunicação com múltiplos clientes. 
O objetivo é permitir que usuários conectados possam enviar perguntas e receber respostas de forma dinâmica.

Recursos

Suporte a múltiplos clientes simultâneos através de threads.

Possibilidade de adicionar, remover ou modificar perguntas e respostas no servidor.

Comunicação em tempo real via sockets TCP.

Estrutura escalável para incluir funcionalidades adicionais, como pontuação, categorias de perguntas ou persistência em banco de dados.

Funciona a partir de QuizServer.java e QuizClient.java, fazendo comuniações cliente servidor, com a possibilidade de adicionar novos peers, os quais teram perguntas que persistem de forma unitária no servidor ao qual o cliente está acessando.

As perguntas são simples, e apresentam para o cliente em questão, tópico, pergunta e alternativas para responder com o número especifico definido para questão, apresentando correto caso tenha acertado a alternativa e errado caso tenha errado.
