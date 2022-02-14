var sessionLogin = JSON.parse(window.localStorage.getItem('sessionLogin'));
if(sessionLogin == null || sessionLogin == undefined || sessionLogin.message != "Login successful."){
    window.location.href = "/pl-login";
}
if(sessionLogin.data.role.id == 1){
    window.location.href = "/";
}

var idBu = sessionLogin.data.bu.id, idTeam = sessionLogin.data.team.id;
var dataPaper = [];
var selectFlag = [];
var selectType = [];
var dataStep = [];
var view_detail_paper = "view-detail-paper";
var view_detail_step = "view-detail-step";

init();
function init() {
  loadDataPaper();
  loadDataSelectType();
  loadDataSelectFlag();
  unActiveView(view_detail_paper);
  unActiveView(view_detail_step);
}

function activeView(viewGroup) {
  viewGroup = "." + viewGroup;
  $(viewGroup + " .detail-infor-active").css("display", "block");
  $(viewGroup + " .detail-infor-unactive").css("display", "none");
  $(viewGroup + " .btn-update").attr("disabled", true);
  $(viewGroup + " .btn-save").attr("disabled", false);
  $(viewGroup + " .btn-cancel").attr("disabled", false);
  $(viewGroup + " .btn-new").attr("disabled", true);
  $(viewGroup + " .unactive-table").css("display", "block");
  $(viewGroup + " .active-table").css("display", "flex");
}

function unActiveView(viewGroup) {
  viewGroup = "." + viewGroup;
  $(viewGroup + " .detail-infor-active").css("display", "flex");
  $(viewGroup + " .detail-infor-unactive").css("display", "block");
  $(viewGroup + " .btn-save").attr("disabled", true);
  $(viewGroup + " .btn-new").attr("disabled", false);
  $(viewGroup + " .btn-cancel").attr("disabled", true);
  $(viewGroup + " .btn-update").attr("disabled", true);
  $(viewGroup + " .unactive-table").css("display", "none");
  $(viewGroup + " .active-table").css("display", "block");
}

$(".btn-new").click(function () {
  var parent = $(this).attr("parent-detail");
  activeView(parent);
  clearViewInput(parent);
});

function clearViewInput(viewGroup) {
  console.log("clearViewInput");
  viewGroup = "." + viewGroup;
  $(viewGroup + " .id-data").val(0);
  $(viewGroup + " input[type='number']").val("");
  $(viewGroup + " input[type='text']").val("");
  $(viewGroup + " select").val(0);
  // $(viewGroup + " .id-data").val(0);
  $(viewGroup + " .tbl-data-content tbody tr").css("background", "#DCE6F1");
}

$(".btn-update").click(function () {
  var parent = $(this).attr("parent-detail");
  activeView(parent);
});

$(".btn-cancel").click(function () {
  var parent = $(this).attr("parent-detail");
  unActiveView(parent);
  clearViewInput(parent);
})

$(".btn-save").click(function () {
  var parent = $(this).attr("parent-detail");
  switch (parent) {
    case view_detail_paper:
      clickBtnSavePaper();
      break;
    case view_detail_step:
      clickBtnSaveStep();
      break;

    default:
      break;
  }

  unActiveView(parent);
  clearViewInput(parent);
})

function loadDataPaper() {
  $.ajax({
    type: "GET",
    url: "/paperless/api/v2/get_list_paper_by_bu_team",
    data: {
      id_bu: idBu,
      id_team: idTeam
    },
    contentType: "application/json; charset=utf-8",
    success: function (response) {
      dataPaper = response['data'];
      var total = response['total'];
      var htmlRow = "";
      if(total > 0){ 
        for (var i = 0; i < dataPaper.length; i++) {
          htmlRow += '<tr row-index="' + i + '">' +
            '<td>' + (i + 1) + '</td>' +
            '<td>' + dataPaper[i].fr_code + '</td>' +
            '<td>' + dataPaper[i].fr_name + '</td>' +
            '<td>' + dataPaper[i].fr_desc + '</td>' +
            '<td>' + dataPaper[i].type_name + '</td>' +
            '<td>' + dataPaper[i].flag_name + '</td>' +
            '</tr>';
        }
        $("#tbl-list-paper tbody").html(htmlRow);
        clickTableRow(view_detail_paper, 1);
      }else{
        $("#tbl-list-paper tbody").html("<tr><td colspan='6'>NO DATA !</td></tr>");
      }
    },
    failure: function (errMsg) {
      console.log(errMsg);
    }
  });
}

function loadDataSelectFlag() {
  $.ajax({
    type: "GET",
    url: "/paperless/api/web/get_list_flag",
    contentType: "application/json; charset=utf-8",
    success: function (response) {
      var status = response["status"];
      var total = response["total"];
      if (status == 1 && total > 0) {
        selectFlag = response["data"];
        var htmlSelect = "";
        for (var i = 0; i < selectFlag.length; i++) {
          htmlSelect += '<option value="' + selectFlag[i]['id'] + '">' + selectFlag[i].name + '</option>'
        }
      }
      $("#select-flag").html(htmlSelect);
    },
    failure: function (error) {
    }
  });
}

function loadDataSelectType() {
  $.ajax({
    type: "GET",
    url: "/paperless/api/v2/get_list_type_paper",
    contentType: "application/json; charset=utf-8",
    success: function (response) {
      var status = response["status"];
      var total = response["total"];
      if (status == 1 && total > 0) {
        selectType = response["data"];
        var htmlSelect = "";
        for (var i = 0; i < selectType.length; i++) {
          htmlSelect += '<option value="' + selectType[i]['id'] + '">' + selectType[i].name + '</option>'
        }
      }
      $("#select-type").html(htmlSelect);
    },
    failure: function (error) {
    }
  });
}

function clickTableRow(viewGroup, checkview) {
  $("." + viewGroup + " .tbl-data-content tbody tr").click(function () {
    $(this).css('background', '#86A8CE').siblings("tr").css('background', '#DCE6F1');
    $("." + viewGroup +" .btn-update").attr("disabled", false);
    var rowIndex = $(this).attr("row-index");
    switch (checkview) {
      case 1:
        loadDataStep(dataPaper[rowIndex]['fr_id']);
        loadDetailPaper(dataPaper[rowIndex]);
        break;
      case 2:
        loadDetailStep(dataStep[rowIndex]);
        break;
      default:
        loadDetailPaper(dataPaper[rowIndex]);
        break;
    }
  });
}

function loadDetailPaper(obj) {
  $("#et-paper-code").val(obj.fr_code);
  $("#et-paper-name").val(obj.fr_name);
  $("#select-type").val(obj.type_id);
  $("#select-flag").val(obj.flag_id);
  $("#et-paper-description").val(obj.fr_desc);
  $("#id-paper").val(obj.fr_id);
  $("#id-bu").val(obj.bu_id);
  $("#id-team").val(obj.team_id);
  $("#id-section").val(obj.section_id);

  $("#id-form-report").val(obj.fr_id);
}

function loadDataStep(idForm) {
  $.ajax({
    type: "GET",
    url: "/paperless/api/v2/get_step_by_id_paper",
    data: {
      id_form: idForm
    },
    contentType: "application/json; charset=utf-8",
    success: function (response) {
      dataStep = response["data"];
      var status = response["status"];
      var total = response["total"];
      var htmlRow = "";
      if (status == 1 && total > 0) {
        for (var i = 0; i < dataStep.length; i++) {
          var desc = JSON.parse(dataStep[i].description);
          htmlRow += '<tr row-index="' + i + '">' +
            '<td>' + (i + 1) + '</td>' +
            '<td>' + dataStep[i].cateName + '</td>' +
            '<td>' + dataStep[i].stepName + '</td>' +
            '<td>' + desc[0]['value'] + '</td>' +
            '<td>' + dataStep[i].typeInput + '</td>' +
            '<td>' + Boolean(dataStep[i].idStatus) + '</td>' +
            '<td>' + Boolean(dataStep[i].index) + '</td>' +
            '<td>' + Boolean(dataStep[i].takePhoto) + '</td>' +
            '</tr>';
        }
        $("#tbl-step tbody").html(htmlRow);
        clickTableRow(view_detail_step, 2);
      }else{
        $("#tbl-step tbody").html("<tr><td colspan='8'>NO DATA !</td></tr>");
      }

    },
    failure: function (error) {

    }
  });
}

function loadDetailStep(obj) {
  var desc = JSON.parse(obj.description);
  $("#et-cate-number").val(obj.cateNum);
  $("#et-cate-name").val(obj.cateName);
  $("#et-step-number").val(obj.stepNum);
  $("#et-step-name").val(obj.stepName);
  $("#et-step-description").val(desc[0]['value']);
  $("#select-step-type").val(obj.typeInput);
  $("#select-step-status").val(obj.idStatus);
  $("#select-step-mo").val(obj.index);
  $("#select-step-take-photo").val(obj.takePhoto);

  $("#id-step").val(obj.id);
  $("#id-form-report").val(obj.idFormReport);
}

function clickBtnSavePaper(){
  var id = $("#id-paper").val();
  var code = $("#et-paper-code").val();
  var name = $("#et-paper-name").val();
  var desc = $("#et-paper-description").val();
  var idType = $("#select-type").val();
  var idFlag = $("#select-flag").val()

  var form = new FormData();
  form.append("id", id);
  form.append("code", code);
  form.append("name", name);
  form.append("desc", desc);
  form.append("id_type", idType);
  form.append("id_flag", idFlag);
  form.append("id_bu", idBu);
  form.append("id_team", idTeam);
  updateDataPaper(form);
}


function clickBtnSaveStep(){
  var id = $("#id-step").val();
  var idPaper = $("#id-form-report").val();
  var cateNum = $("#et-cate-number").val();
  var cateName = $("#et-cate-name").val();
  var stepNum = $("#et-step-number").val();
  var stepName = $("#et-step-name").val();
  var desc = $("#et-step-description").val();

  var type = $("#select-step-type").val();
  var status = $("#select-step-status").val();
  var mo = $("#select-step-mo").val();
  var takePhoto = $("#select-step-take-photo").val();

  if(cateNum.length == 0){
    cateNum = 0;
  }
  if(cateName.length == 0){
    cateName = "";
  }

  var form = new FormData();
  form.append("id", id);
  form.append("id_paper", idPaper);
  form.append("cate_num", cateNum);
  form.append("cate_name", cateName);
  form.append("step_num", stepNum);
  form.append("step_name", stepName);
  form.append("desc", desc);
  form.append("type", type);
  form.append("id_status", status);
  form.append("mo", mo);
  form.append("photo", takePhoto);
  updateDataStep(form);
}

function updateDataStep(data){
  $.ajax({
    "async": true,
    "crossDomain": true,
    "url": "/paperless/api/v2/update_data_step_paper",
    "method": "POST",
    "data": data,
    "processData": false,
    "contentType": false,
    "mimeType": "multipart/form-data",
    success: function (response) {
      response = JSON.parse(response);
      var status = response['status'];
      var message = response['message'];
      var data = response['data'];
      loadDataStep(data['idFormReport']);
      alert(message);
    },
    failure: function (error) {

    }
  });
}

function updateDataPaper(data){
  $.ajax({
    "async": true,
    "crossDomain": true,
    "url": "/paperless/api/v2/update_data_paper",
    "method": "POST",
    "data": data,
    "processData": false,
    "contentType": false,
    "mimeType": "multipart/form-data",
    success: function (response) {
      response = JSON.parse(response);
      var status = response['status'];
      var message = response['message'];
      loadDataPaper();
      alert(message);
    },
    failure: function (error) {

    }
  });
}