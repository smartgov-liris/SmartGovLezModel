import json
import random

nb_establishment = 150

with open('establishments_simturb.json') as json_file:
    min_x = 99999999999.0
    max_x = -99999999999.0
    min_y = 99999999999.0
    max_y = -99999999999.0
    data = json.load(json_file)
    for d in data:
        if float(d['x']) > max_x:
            max_x = float(d['x'])
        if float(d['x']) < min_x:
            min_x = float(d['x'])
        if float(d['y']) > max_y:
            max_y = float(d['y'])
        if float(d['y']) < min_y:
            min_y = float(d['y'])

    print("Max x : ", max_x)
    print("Min x : ", min_x)
    print("Max y : ", max_y)
    print("Min y : ", min_y)

    listEstablishment = []

    for i in range(nb_establishment):
        dico = {}
        dico['ST8'] = '9'
        dico['id'] = str(i)
        dico['name'] = 'habitation' + str(i)
        dico['rounds'] = []
        dico['x'] = str(random.uniform(min_x, max_x))
        dico['y'] = str(random.uniform(min_y, max_y))
        listEstablishment.append(dico)

    print (json.dumps(listEstablishment, sort_keys=True, indent=4))
    
