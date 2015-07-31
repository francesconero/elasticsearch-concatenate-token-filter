# elasticsearch-concatenate-token-filter
Elasticsearch plugin which only provides a TokenFilter that merges tokens in a token stream back into one. Taken from http://elasticsearch-users.115913.n3.nabble.com/Is-there-a-concatenation-filter-td3711094.html

## Install
To install on your current ES node, use the plugin binary provided in the bin folder (on Ubuntu it should be under `/usr/share/elasticsearch/bin`)

    bin/plugin -u https://github.com/francesconero/elasticsearch-concatenate-token-filter/releases/download/v1.0.0/elasticsearch-concatenate-1.0.0.zip -i concatenate
    
## Usage
The plugin provides a token filter of type `concatenate` which has one parameter `token_separator`. Use it in your custom analyzers to merge tokenized strings back into one single token (usually after applying stemming or other token filters).

## Example
Given the custom analyzer (see https://www.elastic.co/guide/en/elasticsearch/guide/current/custom-analyzers.html):

    {
      "analysis" : {
        "filter" : {
          "concatenate" : {
            "type" : "concatenate",
            "token_separator" : "_"
          },
          "custom_stop" : {
            "type": "stop",
            "stopwords": ["and", "is", "the"]
          }
        },
        "analyzer" : {
          "stop_concatenate" : {
            "filter" : [
              "custom_stop",
              "concatenate"
            ],
            "tokenizer" : "standard"
          }
        }
      }
    }
    
Would analyze the string:

    "the fox jumped over the fence"
    
and output the token:

    "fox_jumped_over_fence"
