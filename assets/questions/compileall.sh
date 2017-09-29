export EXEC_PATH="./"
cp compile.sh $EXEC_PATH"compile.sh"

for dir in ./[0-9]*.[0-9]*
do
	echo $dir
	$EXEC_PATH"compile.sh" $dir
done
