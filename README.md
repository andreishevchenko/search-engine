# Search engine

Simple search engine, where you can put document by key, get document by key and find keys of all documents by tokens

## Rest API

Distinguished node provides rest api for:
1. Put with path=/document method=POST parameter=key, parameter=document
```
http://localhost:8080/document?key=key1&document=doc1
```
2. Get with path=/document method=GET parameter=key
```
http://localhost:8080/document?key=key1
```
3. Search with path=/search method=GET parameter=search
```
http://localhost:8080/search?search=doc1
```

### Running

To run application you need to configure application.conf and run BootApp.scala

## Built With

* [Sbt](https://www.scala-sbt.org/) - Dependency Management