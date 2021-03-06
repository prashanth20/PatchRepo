/*
 * Please do not edit this file.
 * It was generated using rpcgen.
 */

#ifndef _DICTIONARY_H_RPCGEN
#define _DICTIONARY_H_RPCGEN

#include <rpc/rpc.h>


#ifdef __cplusplus
extern "C" {
#endif


struct insert_data {
	char word[128];
	char meaning[256];
};
typedef struct insert_data insert_data;

struct search_data {
	char word[128];
};
typedef struct search_data search_data;

struct delete_data {
	char word[128];
};
typedef struct delete_data delete_data;

struct result {
	char data[512];
};
typedef struct result result;

#define ADD_PROG 0x23451111
#define ADD_VERS 1

#if defined(__STDC__) || defined(__cplusplus)
#define ADD 1
extern  result * add_1(insert_data *, CLIENT *);
extern  result * add_1_svc(insert_data *, struct svc_req *);
extern int add_prog_1_freeresult (SVCXPRT *, xdrproc_t, caddr_t);

#else /* K&R C */
#define ADD 1
extern  result * add_1();
extern  result * add_1_svc();
extern int add_prog_1_freeresult ();
#endif /* K&R C */

#define SEARCH_PROG 0x23451112
#define SEARCH_VERS 1

#if defined(__STDC__) || defined(__cplusplus)
#define SEARCH 1
extern  result * search_1(search_data *, CLIENT *);
extern  result * search_1_svc(search_data *, struct svc_req *);
extern int search_prog_1_freeresult (SVCXPRT *, xdrproc_t, caddr_t);

#else /* K&R C */
#define SEARCH 1
extern  result * search_1();
extern  result * search_1_svc();
extern int search_prog_1_freeresult ();
#endif /* K&R C */

#define DELETE_PROG 0x23451113
#define DELETE_VERS 1

#if defined(__STDC__) || defined(__cplusplus)
#define DELETE 1
extern  result * delete_1(delete_data *, CLIENT *);
extern  result * delete_1_svc(delete_data *, struct svc_req *);
extern int delete_prog_1_freeresult (SVCXPRT *, xdrproc_t, caddr_t);

#else /* K&R C */
#define DELETE 1
extern  result * delete_1();
extern  result * delete_1_svc();
extern int delete_prog_1_freeresult ();
#endif /* K&R C */

/* the xdr functions */

#if defined(__STDC__) || defined(__cplusplus)
extern  bool_t xdr_insert_data (XDR *, insert_data*);
extern  bool_t xdr_search_data (XDR *, search_data*);
extern  bool_t xdr_delete_data (XDR *, delete_data*);
extern  bool_t xdr_result (XDR *, result*);

#else /* K&R C */
extern bool_t xdr_insert_data ();
extern bool_t xdr_search_data ();
extern bool_t xdr_delete_data ();
extern bool_t xdr_result ();

#endif /* K&R C */

#ifdef __cplusplus
}
#endif

#endif /* !_DICTIONARY_H_RPCGEN */
