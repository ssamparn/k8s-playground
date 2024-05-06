# YAML Basics

## Comments & Key Value Pairs
- Space after colon `: ` is mandatory to differentiate key and value

```yml
# Defining simple key value pairs
name: sashank
age: 32
city: Hilversum
```

## Dictionary / Map
- Set of properties grouped together after an item
- Equal amount of blank space required for all the items under a dictionary

```yml
person:
  name: sashank
  age: 32
  city: Hilversum
```

## Array / Lists
- Dash indicates an element of an array

```yml
person: # Dictionary
  name: sashank
  age: 32
  city: Hilversum
  hobbiesList1: # List  
    - cycling
    - cooking
  hobbiesList2: [cycling, cooking]   # List with a different notation  
```  

## Multiple Lists
- Dash indicates an element of an array
```yml
person: # Dictionary
  name: kalyan
  age: 23
  city: Hyderabad
  hobbiesList1: # List  
    - cycling
    - cooking
  hobbiesList2: [cycling, cooking]   # List with a different notation  
  friends: # 
    - name: friend1
      age: 22
    - name: friend2
      age: 25            
```  

## Sample Pod Template for Reference

```yml
apiVersion: v1 # String
kind: Pod  # String
metadata: # Dictionary
  name: myapp-pod
  labels: # Dictionary 
    app: myapp         
spec:
  containers: # List
    - name: myapp
      image: ssamantr/docker-hello-world:1.0.0
      ports:
        - containerPort: 80
          protocol: "TCP"
        - containerPort: 81
          protocol: "TCP"
```