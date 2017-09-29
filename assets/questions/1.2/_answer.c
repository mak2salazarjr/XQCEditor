#include <stdio.h>

int main(){
	long long n;
	long long sum=0;
	
	scanf("%lld",&n);
	
	while(n>0){
		sum += n;
		n--;
	}
	
	printf("%lld",sum);
	return 0;
}