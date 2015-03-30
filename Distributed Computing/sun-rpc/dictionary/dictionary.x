struct insert_data {
  char word[128];
  char meaning[256];
};

struct search_data {
  char word[128];
};

struct delete_data {
  char word[128];
};

struct result {
  char data[512];
};

program ADD_PROG {
  version ADD_VERS {
    result ADD(insert_data) = 1;
  } = 1;
} = 0x23451111;

program SEARCH_PROG {
  version SEARCH_VERS {
    result SEARCH(search_data) = 1;
  } = 1;
} = 0x23451112;

program DELETE_PROG {
  version DELETE_VERS {
    result DELETE(delete_data) = 1;
  } = 1;
} = 0x23451113;
