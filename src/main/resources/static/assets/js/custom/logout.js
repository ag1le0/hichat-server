  function logout(){
        var confirm = window.confirm("Are you sure Logout?");
        if(confirm == true){
            window.localStorage.removeItem('sessionLogin');
            window.location.href = "/pl-login";
        }
    }
    var time = new Date().getTime();
    $(document.body).bind("mousemove keypress", function(e){
        time = new Date().getTime();
    });

    document.addEventListener("mousemove", resetTimer, false);
    document.addEventListener("mousedown", resetTimer, false);
    document.addEventListener("keypress", resetTimer, false);
    document.addEventListener("touchmove", resetTimer, false);
    document.addEventListener("onscroll", resetTimer, false);
    function resetTimer() {
        time = new Date().getTime();
    }

    function destroySesstion(){
        if(new Date().getTime() - time > 1800000){
            window.localStorage.removeItem('sessionLogin');
            window.location.reload(true);
        }else {
            setTimeout(destroySesstion, 10000);
        }
    }
    setTimeout(destroySesstion, 10000);