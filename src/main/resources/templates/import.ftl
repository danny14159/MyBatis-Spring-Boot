<html>
<body>
<button onclick="click_to_run()">Click To Run</button>
<script src="/js/jquery-1.11.1.min.js"></script>
<script>
    function run(r,rt,f) {
        console.log(r,rt);
        $.post("",{r:r,rt:rt},function(data){
            console.log(data);
            if(f) f();
        },'json');
    }
    function click_to_run() {
        run('sh1','b',function () {
            run('sh2',null,function () {
                run('sh3',null,function () {
                    run('dl1',null,null);
                });
            });
        });
    }
</script>
</body>
</html>