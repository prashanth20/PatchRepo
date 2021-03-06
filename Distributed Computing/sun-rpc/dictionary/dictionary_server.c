/*
 * This is sample code generated by rpcgen.
 * These are only templates and you can use them
 * as a guideline for developing your own functions.
 */

#include <string.h>
#include <stdlib.h>

#include "dictionary.h"

typedef struct _DICTIONARY {
  char word[128];
  char meaning[256];
  struct _DICTIONARY* link;
} DICTIONARY;

DICTIONARY* root = NULL;

DICTIONARY* create_node(char* word, char* meaning)
{
  DICTIONARY* new_word = malloc(sizeof(DICTIONARY));
  strncpy(new_word->word, word, strlen(word));
  new_word->word[strlen(word)] = '\0';
  strncpy(new_word->meaning, meaning, strlen(meaning));
  new_word->meaning[strlen(meaning)] = '\0';
  new_word->link = NULL;
  return new_word;
}

void AddWord(char* word, char* meaning, char* result)
{
  DICTIONARY* next_node = NULL;
  DICTIONARY* new_node = NULL;
  DICTIONARY* cur_node = NULL;

  if (root == NULL) {
    root = create_node(word, meaning);
    sprintf(result, "Word : [%s] Meaning : [%s] added\n", word, meaning);
    return;
  }

  for (next_node = root; next_node != NULL; next_node = next_node->link)
  {
    if (strcasecmp(next_node->word, word) == 0) {
      printf("word already exist\n");
      sprintf(result, "Word : [%s] Meaning : [%s] already exist\n", word, meaning);
      return;
    }
    cur_node = next_node;
  }

  new_node = create_node(word, meaning);
  cur_node->link = new_node;
  sprintf(result, "Word : [%s] Meaning : [%s] added\n", word, meaning);
}

void SearchWord(char* word, char* result)
{
  DICTIONARY* search = NULL;

  if (root == NULL) {
    printf("Dictionary is empty, try searching after adding some element\n");
    strcpy(result, "Dictionary is empty, try searching after adding some element\n");
    return;
  }

  for (search = root; search != NULL; search = search->link)
  {
    if (strcasecmp(search->word, word) == 0) {
      printf("word found !!!\n");
      printf("word : [%s] meaning : [%s]\n", search->word, search->meaning);
      sprintf(result, "word : [%s] meaning : [%s]\n", search->word, search->meaning);
      return;
    }
  }

  printf("word : [%s] not found \n", word);
  sprintf(result, "word : [%s] not found \n", word);
}

void DeleteWord(char* word, char* result)
{
  DICTIONARY* delete = NULL;
  DICTIONARY* prev = NULL;

  if (root == NULL)
  {
    printf("Dictionary is empty, try deleting after adding some element \n");
    strcpy(result, "Dictionary is empty, try deleting after adding some element\n");
    return;
  }

  prev = root;
  if (strcasecmp(root->word, word) == 0) {
    printf("Deleting word : [%s]\n", word);
    if (root->link != NULL) {
      strcpy(root->word, root->link->word);
      strcpy(root->meaning, root->link->meaning);
      root = root->link;
      free(prev);
      sprintf(result, "Deleted word [%s] \n", word);
   } else {
      free(root);
      sprintf(result, "Deleted word [%s] \n", word);
      root = NULL, prev = NULL;
   }
   return;
  } else {
    for (delete = root; delete != NULL; delete = delete->link)
    {
      if (strcasecmp(delete->word, word) == 0)
      {
        sprintf(result, "Deleted word [%s] \n", word);
        prev->link = delete->link;
        free(delete);
        return;
      }
      prev = delete;
    }
  }

  printf("Word : [%s] not found \n", word);
  sprintf(result, "Word : [%s] not found \n", word);
}

result *
add_1_svc(insert_data *argp, struct svc_req *rqstp)
{

  static result  result;

  printf("argument recieved : [%s] [%s] \n", argp->word, argp->meaning);
  AddWord(argp->word, argp->meaning, result.data);

  printf("%s\n", result.data);

	return &result;
}

result *
search_1_svc(search_data *argp, struct svc_req *rqstp)
{
	static result  result;

  printf("argument recieved : [%s] \n", argp->word);
  SearchWord(argp->word, result.data);

  printf("%s\n", result.data);

	return &result;
}

result *
delete_1_svc(delete_data *argp, struct svc_req *rqstp)
{
	static result  result;

  printf("argument recieved : [%s] \n", argp->word);
  DeleteWord(argp->word, result.data);

  printf("%s\n", result.data);

	return &result;
}
