# elasticsearch-concatenate-token-filter
Elasticsearch plugin which only provides a TokenFilter that merges tokens in a token stream back into one. Taken from http://elasticsearch-users.115913.n3.nabble.com/Is-there-a-concatenation-filter-td3711094.html

## Install
To install on your current ES node, use the plugin binary provided in the bin folder (on Ubuntu it should be under `/usr/share/elasticsearch/bin`)
For Elasticsearch 2.2.0 use

    sudo bin/plugin install francesconero/elasticsearch-concatenate-token-filter/2.2.0

For Elasticsearch 2.2.2 use

    sudo bin/plugin install francesconero/elasticsearch-concatenate-token-filter/2.2.2

For Elasticsearch 2.3.3 use

    sudo bin/plugin install francesconero/elasticsearch-concatenate-token-filter/2.3.3

For Elasticsearch 5.2.0 use

    sudo bin/elasticsearch-plugin install https://github.com/bomberby/elasticsearch-concatenate-token-filter/raw/es-5.2.0/elasticsearch-concatenate-5.2.0.zip

## Usage
The plugin provides a token filter of type `concatenate` which has one parameter `token_separator`. Use it in your custom analyzers to merge tokenized strings back into one single token (usually after applying stemming or other token filters).
### Arrays
When saving arrays of strings to a field, these are handled in elasticsearch as separate tokens, so this filter would collapse all the elements of the array into one, and usually you don't want that to happen. As a workaround you can set `position_offset_gap` on the field to a high number and pass the same number as the `increment_gap` parameter to the filter, which then only concatenates all tokens closer than this value.

## Example
Given the custom analyzer (see https://www.elastic.co/guide/en/elasticsearch/guide/current/custom-analyzers.html):

```javascript
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
```
the string:

    "the fox jumped over the fence"
    
would be analyzed as:

    "fox_jumped_over_fence"

## To build new version
  Change relevant ES versions in following files:
  ```
  pom.xml
  plugin-descriptor.properties
  target/classes/es-plugin.properties
  ```
  If any major change was done to ES way of writing plugins, these will need to change as well, please look at example plugins
  run `mvn clean install` and copy result files from target/releases to root for easier linking
