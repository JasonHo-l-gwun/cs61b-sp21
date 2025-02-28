# A helper file include create file,add file,rm file and commit file
> init
<<<
+ wug.txt wug.txt
+ wug2.txt wug2.txt
+ notwug.txt notwug.txt
+ wug3.txt wug3.txt
+ wug4.txt wug4.txt
> add notwug.txt
<<<
> add wug2.txt
<<<
> add wug3.txt
<<<
> commit "test"
<<<
- wug3.txt
+ notwug.txt wug.txt
> rm wug2.txt
<<<
> add wug.txt
<<<