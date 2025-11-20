# App-Gerador-de-Senhas
Desenvolvimento de um App Android para gerar senhas baseadas na data selecionada, com versão DD/MM e MM/DD. Projeto realizado no Android Studio, e com APK gerado, com inicialização na data atual e podendo selecionar outra data por um calendário se necessário. Além disso as senhas podem ser copiadas para a área de transferência do dispositivo.

## Funcionamento:
O gerador de senhas separa os dígitos do dia e do mês, onde dia = D1 D2 e mês = M1 M2, além disso é usado a soma hexadecimal de todos os dígitos:

Hex = hex(D1 + D2 + M1 + M2), sendo Hex uma string de tamanho 2.

Após isso é realizada a criação da senha juntando tudo da seguinte forma:

Senha = D1 M1 D2 M2 Hex

## Divisão MM/DD e DD/MM
A senha a ser usada muda de acordo com o padrão de data utilizado, então o aplicativo mostra as duas variações:

DD/MM: Senha = D1 M1 D2 M2 Hex

MM/DD: Senha = M1 D1 M2 D2 Hex
