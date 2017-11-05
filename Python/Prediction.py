

# Importing the libraries
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import socket
import csv

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

server_address = ("10.46.0.125",2347)
print('starting up on %s port %s' %server_address)
sock.bind(server_address)
sock.listen(1)

print ( 'waiting for  a connection')
connection, client_address = sock.accept()
print ('connection from', client_address)
city = connection.recv(16)
road = connection.recv(16)
print ('received %s ' %city.decode("utf-8") )
print ('received %s ' %road.decode("utf-8") )
city2 = float(city.decode("utf-8"))
road2 = float(road.decode("utf-8"))
        
# Importing the dataset
dataset = pd.read_csv('total.csv')
X = dataset.iloc[:, :-1].values
y = dataset.iloc[:, 4].values

from sklearn.preprocessing import LabelEncoder, OneHotEncoder
labelencoderx = LabelEncoder()
X[:,0] = labelencoderx.fit_transform(X[:,0])
onehotencoder = OneHotEncoder(categorical_features=[0])
X = onehotencoder.fit_transform(X).toarray()
X = X[:,1:]

# Splitting the dataset into the Training set and Test set
from sklearn.cross_validation import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 1/14, random_state = 42)
app_avg = 0
sayac=0;
while (sayac<len(X_test)):
    app_avg = app_avg + X[2,sayac]
    sayac = sayac + 1

app_avg = app_avg/sayac    

es_avg = 0
sayac2=0;
while (sayac2<len(X_test)):
    es_avg = es_avg + X[2,sayac2]
    sayac2 = sayac2 + 1

es_avg = es_avg/sayac2 


# Feature Scaling
from sklearn.preprocessing import StandardScaler
sc_X = StandardScaler()
X_train = sc_X.fit_transform(X_train)
X_test = sc_X.transform(X_test)

from sklearn.linear_model import LinearRegression
regressor =LinearRegression()
regressor.fit(X_train,y_train)


A=[city2,app_avg,es_avg,road2]  
A = sc_X.fit_transform(A)
ypred = regressor.predict(A)

print (ypred)

to_Send = str(ypred[0]).encode()

connection.send(to_Send)
print ("Send Check")

## dataları al, arraya ekle
print ("Waiting for new data")
new_acc = connection.recv(16)
new_engi = connection.recv(16)
new_odo = connection.recv(16)
new_fuel = connection.recv(16)


new_acc2 = float(new_acc.decode("utf-8"))
print (new_acc2)
new_engi2 = float(new_engi.decode("utf-8"))
print (new_engi2)
 
new_fuel = new_fuel[:-3]
print (new_fuel[6:16]) 


"""fd = open('total.csv','a')
fd.write(new)
fd.close()"""




connection.close()