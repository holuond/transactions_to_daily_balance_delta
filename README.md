# Transactions to Daily Balance Delta
This simple script transforms a list of financial transactions (date & amount) into a list of pairs: date --> total balance change.
  
## Example input:
````
29.01.2020,300
29.01.2020,-100
31.01.2020,99
````
## Example output:
````
2020-01-29,200
2020-01-30,0
2020-01-31,99
````
