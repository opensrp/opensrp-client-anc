#include<iostream>
#include<algorithm>
using namespace std;
int  main(){
	char data[]={'a','r','b','k','m'};
	char key='a';
    int bs= binary_search(data,data+5,key);
	
	cout << bs << " ";
	
}
