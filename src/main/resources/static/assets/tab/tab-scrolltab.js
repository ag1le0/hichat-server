//CREATE TAB MENU
var arrTab = [];
var nameCurrentTab = '';
function pushMenu(value) {
    var checkExists = arrTab.includes(value);
    if (!checkExists) {
        arrTab.push(value);
        return true;
    }
    return false;
}
function popMenu(strName) {
    var checkExists = arrTab.includes(strName);
    if ($('#' + strName + strName).hasClass('activefii')) {
        if (checkExists) {
            var index = arrTab.indexOf(strName);
            // console.log(index)
            if ((0 < index) && (index < (arrTab.length - 1))) {
                if (index !== -1) arrTab.splice(index, 1);
                showContent(arrTab[index]);
            } else if ((index != 0) && (index == (arrTab.length - 1))) {
                if (index !== -1) arrTab.splice(index, 1);
                showContent(arrTab[index - 1]);
            }
            if (index == 0) {
                if (index !== -1) arrTab.splice(index, 1);
                hideAllContentTab(strName);
            }
        }
    } else {
        if (checkExists) {
            var index = arrTab.indexOf(strName);
            if (index !== -1) arrTab.splice(index, 1);
            document.getElementById(strName).style.display = "none";
        }
    }
}

function addTabTest(nameTitle, strName, url) {
    nameCurrentTab = strName;
    // $('.wrapper').css('display','block');
    document.getElementById("idWrapper").style.display="block";
    $('.tab').removeClass('hiddenImage');
    $('.content-menu-tab').removeClass('hiddenImage');
    $('.tocontentfii').addClass('hiddenImage')
    /// var link = url.getAttribute('data-href');
    if (pushMenu(strName)) {
        $(".mTab").append('<div id="' + strName + strName + '" class="ctTabs tablinks activefii">' +
            '<div  class="' + strName + '"  onclick=showContent("' + strName + '")>' +
            '<span>' + nameTitle + '</span>' +
            '</div>' +
            '<span class="closeTabsFii" onclick="closeTabs(this)" nameContent="' + strName + '">x</span>' +
            '</div>');
        hideAllContentTab(strName);
        $(".content-menu-tab").append('<div id="' + strName + '" class="tabcontent mTabContent " style="display: block;">' +
            '<iframe id="' + strName + strName + strName + '"  class="iframeFii" scrolling="auto" frameborder="0"  src="' + url + '" style="width:100%;height:100%;"></iframe>' +
            '</div>');
        widthTabs(strName);
    } else {
        hideAllContentTab(strName);
        document.getElementById(strName).style.display = "block";
    }
    $('.ctTabs').removeClass('activefii');
    $('#' + strName + strName).addClass('activefii');
    var height = screen.height;
    $('.iframeFii').css('height', height + 'px');
    reAdjust();
}

function hideAllContentTab(strName) {
    tabcontent = document.getElementsByClassName("mTabContent");
    var x = strName + strName + strName;
    var height = screen.height;
    for (i = 0; i < tabcontent.length; i++) {
        if (tabcontent[i].children[0].id != x) {
            tabcontent[i].style.display = "none";
            tabcontent[i].children[0].style.width = "90%";
            //  tabcontent[i].children[0].style.height = height - 2 + "px";
        }

    }
}
function showContent(strName) {
    var x = strName + strName + strName;
    nameCurrentTab = strName;
    $('.ctTabs').removeClass('activefii');
    $('#' + strName + strName).addClass('activefii');
    hideAllContentTab(strName);
    document.getElementById(strName).style.display = "block";
    $('#' + strName + '>.iframeFii').get(0).contentWindow.postMessage('resize', '*');
    // var height = screen.height;
    document.getElementById(x).style.width = "100%";
    //  document.getElementById(x).style.height = height + 2 + 'px';
    // $('#' + x).style.width = "100%";
    // $('#' + strName + strName + strName).style.height = height + 'px';


}

function closeTabs(elem) {
    var name = $(elem).attr("nameContent");
    popMenu(name);
    var x = $(elem).parent('div');
    var y = x[0].id
    widthTabsWhenClose(y);
    $(elem).parent('div').remove();
    // document.getElementById(name).style.display = "none";
    if (arrTab.length == 0) {
        $('.tab').addClass('hiddenImage');
        $('.content-menu-tab').addClass('hiddenImage');
        $('.tocontentfii').removeClass('hiddenImage')
    }
    reAdjust();
}
function openCity(evt, cityName) {
    // Declare all variables
    var i, tabcontent, tablinks;

    // Get all elements with class="tabcontent" and hide them
    tabcontent = document.getElementsByClassName("mTabContent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }

    // Get all elements with class="tablinks" and remove the class "active"
    tablinks = document.getElementsByClassName("mTabContent");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" activefii", "");
    }

    // Show the current tab, and add an "active" class to the button that opened the tab
    document.getElementById(cityName).style.display = "block";
    evt.currentTarget.className += " activefii";
}

$(document).ready(function () {
    var elem = null;
    $('.fullScreenFii').click(function () {
        if (nameCurrentTab == '') {
            elem = document.body;
        } else {
            elem = document.getElementById(nameCurrentTab);
        }
        openFullscreenFii();
    });
    function openFullscreenFii() {
        if (elem.requestFullscreen) {
            elem.requestFullscreen();
        } else if (elem.mozRequestFullScreen) { /* Firefox */
            elem.mozRequestFullScreen();
        } else if (elem.webkitRequestFullscreen) { /* Chrome, Safari & Opera */
            elem.webkitRequestFullscreen();
        } else if (elem.msRequestFullscreen) { /* IE/Edge */
            elem.msRequestFullscreen();
        };
    };

    $('.refreshPageTab').click(function () {
        if (nameCurrentTab == '') {
            elem = document.body;
        } else {
            elem = document.getElementById(nameCurrentTab).getElementsByTagName('iframe')[0].getAttribute('src');
        }
        $('#' + nameCurrentTab + " iframe").attr('src', $('#' + nameCurrentTab + " iframe").attr('src'));
        //elem.contentDocument.location.reload(true);
    });
});

// END CREATE TAB MENU


/// SCROLL MENU TAB -----------------
var hidWidth;
var scrollBarWidths = 40;

// var awidthOfList = function () {
//     var itemsWidth = 0;
//     $('.list div.ctTabs').each(function () {
//         alert(11)
//         var itemWidth = $(this).outerWidth();
//         itemsWidth += itemWidth;
//     });
//     return itemsWidth;
// };

var widthTab = 0;
function widthTabs(idTab) {
    widthTab += $('#' + idTab + idTab).outerWidth();
    return widthTab;
}
function widthTabsWhenClose(idTab) {
    //   alert($('#' + idTab).outerWidth());
    var a = $('#' + idTab).outerWidth();
    widthTab -= a;
    return widthTab;
}
var widthOfHidden = function () {
    var b = (($('.wrapper').outerWidth()) - widthTab - getLeftPosi()) - scrollBarWidths;
    return b;
};

var getLeftPosi = function () {
    return $('.list').position().left;
};

var reAdjust = function () {
    //  console.log($('.wrapper').outerWidth())
    if (($('.wrapper').outerWidth()) < widthTab) {
        $('.scroller-right').show();
    }
    else {
        $('.scroller-right').hide();
    }

    if (getLeftPosi() < 0) {
        $('.scroller-left').show();
    }
    else {
        $('.item').animate({ left: "-=" + getLeftPosi() + "px" }, 'slow');
        $('.scroller-left').hide();
    }
}

reAdjust();

$(window).on('resize', function (e) {
    reAdjust();
});

$('.scroller-right').click(function () {

    $('.scroller-left').fadeIn('slow');
    $('.scroller-right').fadeOut('slow');

    $('.list').animate({ left: "+=" + widthOfHidden() + "px" }, 'slow', function () {

    });
});

$('.scroller-left').click(function () {

    $('.scroller-right').fadeIn('slow');
    $('.scroller-left').fadeOut('slow');

    $('.list').animate({ left: "-=" + getLeftPosi() + "px" }, 'slow', function () {

    });
});
// END SCROLL MENU TAB
