if [ $# != 1 ] ; then 
	echo "no input files."
	exit 1;
fi

export EXEC_PATH="./"

echo compile $1 
gcc -static -o $EXEC_PATH$1.elf $1/_answer.c

for i in {1..10} 
do
	$EXEC_PATH$1.elf < $1/input$i.txt > $1/output$i.txt
	echo $1"/output$i.txt"
done
rm -f $1.elf
