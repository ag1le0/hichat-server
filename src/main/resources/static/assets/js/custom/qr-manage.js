
var sessionLogin = JSON.parse(window.localStorage.getItem('sessionLogin'));
if(sessionLogin == null || sessionLogin == undefined || sessionLogin.message != "Login successful."){
    window.location.href = "/pl-login";
}
if(sessionLogin.data.role.id != 3){
    window.location.href = "/paperless";
}

var idBu = sessionLogin.data.bu.id, idTeam = sessionLogin.data.team.id;
var checkLine, checkStation, checkQr;
checkLine = checkStation = checkQr = false;
var dataLine = [];
var groupsQr = [];
var dataQrCode = [];
var groupsLine = [];
var typeLine = [];
var qrCodeLine = [];
var qrCodeStation = [];

var VIEW_DETAIL_LINE = "view-detail-line";
var VIEW_DETAIL_STATION = "view-detail-group";
var VIEW_DETAIL_QRCODE = "view-detail-qrcode";

init();

function init() {
  loadDataLine();
  loadDataStation();
  loadDataQrcode();
  loadGroupLine();
  loadTypeLine();
  canvasQrcode();
  unActiveView(VIEW_DETAIL_LINE);
  unActiveView(VIEW_DETAIL_STATION);
  unActiveView(VIEW_DETAIL_QRCODE);
}

function loadDataLine() {
  $.ajax({
    type: "GET",
    url: "/paperless/api/v2/get_list_line_by_bu_team",
    data: {
      id_bu: idBu,
      id_team: idTeam
    },
    contentType: "application/json; charset=utf-8",
    success: function (response) {
      dataLine = response['data'];

      var htmlRow = "";
      var htmlSelect = "";
      for (var i = 0; i < dataLine.length; i++) {
        htmlRow += '<tr row-index="' + i + '">'
          + '<td>' + (i + 1) + '</td>'
          + '<td>' + dataLine[i]['name'] + '</td>'
          + '<td>' + dataLine[i]['display_name'] + '</td>'
          + '<td>' + dataLine[i]['floor'] + '</td>'
          + '<td>' + dataLine[i]['position'] + '</td>'
          + '<td>' + dataLine[i]['f_name'] + '</td>'
          + '<td>' + dataLine[i]['gl_name'] + '</td>'
          + '<td>' + dataLine[i]['status_name'] + '</td>'
        '</tr>';

        htmlSelect += '<option value="' + dataLine[i]['id'] + '">' + dataLine[i].name + '</option>'

      }

      $("#select-qrcode-line").html(htmlSelect);
      $("#tbl-line tbody").html(htmlRow);
      checkLine = true;
      addClickTableRow(VIEW_DETAIL_LINE, 1);
    },
    failure: function (errMsg) {
      console.log(errMsg);
    }
  });
}

function loadDataStation() {
  $.ajax({
    type: "GET",
    url: "/paperless/api/web/get_list_group_qr_code_by_bu_team",
    data: {
      id_bu: idBu,
      id_team: idTeam
    },
    contentType: "application/json; charset=utf-8",
    success: function (response) {
      groupsQr = response['data'];
      var htmlRow = "";
      var htmlSelect = "";
      for (var i = 0; i < groupsQr.length; i++) {
        htmlRow += '<tr row-index="' + i + '">' +
          '<td>' + (i + 1) + '</td>' +
          '<td>' + groupsQr[i]['name'] + '</td>' +
          '<td>' + groupsQr[i]['description'] + '</td>' +
          '</tr>';
        htmlSelect += '<option value="' + groupsQr[i]['id'] + '">' + groupsQr[i].name + '</option>'
      }
      $("#select-qrcode-station").append(htmlSelect);
      $("#tbl-group tbody").html(htmlRow);
      checkStation = true;
      addClickTableRow(VIEW_DETAIL_STATION, 2);
    },
    failure: function (errMsg) {
      console.log(errMsg);
    }
  });
}
function loadDataQrcode() {
  $.ajax({
    type: "GET",
    url: "/paperless/api/v2/get_list_qr_code_by_bu_team",
    data: {
      id_bu: idBu,
      id_team: idTeam
    },
    contentType: "application/json; charset=utf-8",
    success: function (response) {
      dataQrCode = response["data"];
      var htmlRow = "";
      for (var i = 0; i < dataQrCode.length; i++) {

        htmlRow += '<tr row-index="' + i + '">' +
          '<td>' + (i + 1) + '</td>' +
          '<td>' + dataQrCode[i].qc_code + '</td>' +
          '<td>' + dataQrCode[i].qc_dis_name + '</td>' +
          '<td>' + dataQrCode[i].l_name + '</td>' +
          '<td>' + dataQrCode[i].gqc_name + '</td>' +
          '<td>' + dataQrCode[i].qc_position + '</td>' +
          '<td>' + dataQrCode[i].status_name + '</td>' +
          '<td>' + dataQrCode[i].qc_desc + '</td>' +
          '</tr > ';
      }
      $("#tbl-qrcode tbody").html(htmlRow);
      checkQr = true;
      addClickTableRow(VIEW_DETAIL_QRCODE, 3);
    },
    failure: function (errMsg) {
      console.log(errMsg);
    }
  });
}

function loadDetailLine(obj, viewGroup) {
  $("#et-name-line").val(obj.name);
  $("#et-display-line").val(obj.display_name);
  $("#et-floor-line").val(obj.floor);
  $("#et-position-line").val(obj.position);
  $("#select-group-line").val(obj.gl_id);
  $("#select-type-line").val(obj.f_id);
  viewGroup = "." + viewGroup;
  $(viewGroup + " .id-data").val(obj.id);
}
function loadDetailGroup(obj, viewGroup) {
  $("#et-name-group").val(obj.name);
  $("#et-description-group").val(obj.description);
  viewGroup = "." + viewGroup;
  $(viewGroup + " .id-data").val(obj.id);
}
function loadDetailQrcode(obj, viewGroup) {
  $("#et-name-qrcode").val(obj.qc_code);
  $("#et-display-qrcode").val(obj.qc_dis_name);
  $("#et-position-qrcode").val(obj.qc_position);
  $("#et-description-qrcode").val(obj.qc_desc);
  $("#select-qrcode-line").val(obj.l_id);
  $("#select-qrcode-station").val(obj.gqc_id);
  $("#select-qrcode-status").val(obj.qc_status);
  canvasQrcode(obj.qc_code);
  viewGroup = "." + viewGroup;
  console.log(obj);
  console.log(viewGroup);
  $(viewGroup + " .id-data").val(obj.qc_id);
}

function addClickTableRow(viewGroup, checkView) {
  $("." + viewGroup + " .tbl-data-content tbody tr").click(function () {
    $(this).css('background', '#86A8CE').siblings("tr").css('background', '#DCE6F1');

    var rowIndex = $(this).attr("row-index");
    $("." + viewGroup + " .btn-update").attr("disabled", false);
    switch (checkView) {
      case 1:
        loadDetailLine(dataLine[rowIndex], viewGroup);
        break;
      case 2:
        loadDetailGroup(groupsQr[rowIndex], viewGroup);
        break;
      case 3:
        loadDetailQrcode(dataQrCode[rowIndex], viewGroup);
        break;
      default:
        loadDetailLine(dataLine[rowIndex], viewGroup);
        break;
    }
    //Active button update
  });
}
function canvasQrcode() {
  var txt = $("#et-name-qrcode").val();
  createQrCode(txt);
}


$(".btn-new").click(function () {
  var parent = $(this).attr("parent-detail");
  activeView(parent);
  clearViewInput(parent);
});

function clearViewInput(viewGroup) {
  viewGroup = "." + viewGroup;
  $(viewGroup + " input").val("");
  $(viewGroup + " select").val(0);

  $(viewGroup + " .id-data").val(0);
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
    case VIEW_DETAIL_LINE:
      clickBtnSaveLine();
      break;
    case VIEW_DETAIL_STATION:
      clickBtnSaveStation();
      //do st
      break;
    case VIEW_DETAIL_QRCODE:
    clickBtnSaveQrcode();
      break;

    default:
      break;
  }

  unActiveView(parent);
  clearViewInput(parent);
})

// $(".active-table").css("display", "flex"); //in-visibility
// $(".unactive-table").css("display", "none");

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

function loadGroupLine() {
  $.ajax({
    type: "GET",
    url: "/paperless/api/v1/get_list_group_line_by_bu_team",
    data: {
      id_bu: idBu,
      id_team: idTeam
    },
    contentType: "application/json; charset=utf-8",
    success: function (response) {
      var status = response["status"];
      var total = response["total"];
      if (status == 1 && total > 0) {
        groupsLine = response['data'];
        var htmlSelect = "";
        for (i = 0; i < groupsLine.length; i++) {
          htmlSelect += '<option value="' + groupsLine[i]['id'] + '">' + groupsLine[i].name + '</option>'
        }
      }
      $("#select-group-line").append(htmlSelect);
    },
    failure: function (error) {

    }
  });

}

function loadTypeLine() {
  $.ajax({
    type: "GET",
    url: "/paperless/api/web/get_list_flag",
    data: {
      id_type: 2
    },
    contentType: "application/json; charset=utf-8",
    success: function (response) {
      var status = response["status"];
      var total = response["total"];
      if (status == 1 && total > 0) {
        typeLine = response['data'];
        var htmlSelect = "";
        for (i = 0; i < typeLine.length; i++) {
          htmlSelect += '<option value="' + typeLine[i]['id'] + '">' + typeLine[i].name + '</option>'
        }
      }
      $("#select-type-line").html(htmlSelect);
    },
    failure: function (error) {

    }
  });
}

function createQrCode(qrValue) {
  var qr = new QRious({
    element: document.getElementById('qr-ex'),
    value: qrValue,
    background: '#EEEDED', // background color
    foreground: 'black', // foreground color
    backgroundAlpha: 1,
    foregroundAlpha: 1,
    level: 'L', // Error correction level of the QR code (L, M, Q, H)
    mime: 'image/png', // MIME type used to render the image for the QR code
    size: 95, // Size of the QR code in pixels.
    padding: null // padding in pixels
  });
}

function clickBtnSaveLine() {
  var id = $("#id-line").val();
  var name = $("#et-name-line").val();
  var display = $("#et-display-line").val();
  var floor = $("#et-floor-line").val();
  var position = $("#et-position-line").val();
  var group = $("#select-group-line").val();
  var type = $("#select-type-line").val();

  var form = new FormData();
  form.append("id", id);
  form.append("name", name);
  form.append("display", display);
  form.append("floor", floor);
  form.append("position", parseInt(position));
  form.append("id_group", parseInt(group));
  form.append("id_flag", parseInt(type));
  form.append("id_bu", idBu);
  form.append("id_team", idTeam);

  if (id != 0) {
    updateDataLine(form);
  } else {
    insertDataLine(form);
  }
}

function insertDataLine(data) {
  $.ajax({
    "async": true,
    "crossDomain": true,
    "url": "/paperless/api/v2/insert_line",
    "method": "POST",
    "data": data,
    "processData": false,
    "contentType": false,
    "mimeType": "multipart/form-data",
    success: function (response) {
      response = JSON.parse(response);
      var status = response['status'];
      var message = response['message'];
      loadDataLine();
      alert(message);
    },
    failure: function (error) {

    }
  });
}

function updateDataLine(data) {
  $.ajax({
    "async": true,
    "crossDomain": true,
    "url": "/paperless/api/v2/update_line",
    "method": "POST",
    "data": data,
    "processData": false,
    "contentType": false,
    "mimeType": "multipart/form-data",
    success: function (response) {
      response = JSON.parse(response);
      var status = response['status'];
      var message = response['message'];
      loadDataLine();
      alert(message);
    },
    failure: function (error) {

    }
  });
}

//
function clickBtnSaveStation() {
  var id = $("#id-station").val();
  var name = $("#et-name-group").val();
  var description = $("#et-description-group").val();

  var form = new FormData();
  form.append("id", id);
  form.append("name", name);
  form.append("desc", description);
  form.append("id_bu", idBu);
  form.append("id_team", idTeam);

  if (id != 0) {
    updateDataStation(form);
  } else {
    insertDataStation(form);
  }
}

function insertDataStation(data) {
  $.ajax({
    "async": true,
    "crossDomain": true,
    "url": "/paperless/api/v2/insert_group_qr",
    "method": "POST",
    "data": data,
    "processData": false,
    "contentType": false,
    "mimeType": "multipart/form-data",
    success: function (response) {
      response = JSON.parse(response);
      var status = response['status'];
      var message = response['message'];
      loadDataStation();
      alert(message);
    },
    failure: function (error) {

    }
  });

}
function updateDataStation(data) {
  $.ajax({
    "async": true,
    "crossDomain": true,
    "url": "/paperless/api/v2/update_group_qr",
    "method": "POST",
    "data": data,
    "processData": false,
    "contentType": false,
    "mimeType": "multipart/form-data",
    success: function (response) {
      response = JSON.parse(response);
      var status = response['status'];
      var message = response['message'];
      loadDataStation();
      alert(message);
    },
    failure: function (error) {

    }
  });
}

function clickBtnSaveQrcode() {
  var id = $("#id-qc").val();
  var code = $("#et-name-qrcode").val();
  var display = $("#et-display-qrcode").val();
  var position = $("#et-position-qrcode").val();
  var line = $("#select-qrcode-line").val();
  var station = $("#select-qrcode-station").val();
  var status = $("#select-qrcode-status").val();
  var description = $("#et-description-qrcode").val();

  var form = new FormData();
  form.append("id", id);
  form.append("code", code);
  form.append("display", display);
  form.append("position", parseInt(position));
  form.append("id_line", parseInt(line));
  form.append("id_group", parseInt(station));
  form.append("id_status", parseInt(status));
  form.append("desc", description);
  form.append("id_bu", idBu);
  form.append("id_team", idTeam);
  if (id != 0) {
    updateDataSQrcode(form);
  } else {
    insertDataQrcode(form);
  }
}

function insertDataQrcode(data) {
  $.ajax({
    "async": true,
    "crossDomain": true,
    "url": "/paperless/api/v2/insert_qr_code",
    "method": "POST",
    "data": data,
    "processData": false,
    "contentType": false,
    "mimeType": "multipart/form-data",
    success: function (response) {
      response = JSON.parse(response);
      var status = response['status'];
      var message = response['message'];
      loadDataQrcode();
      alert(message);
    },
    failure: function (error) {

    }
  });

}

function updateDataSQrcode(data) {
  $.ajax({
    "async": true,
    "crossDomain": true,
    "url": "/paperless/api/v2/update_qr_code",
    "method": "POST",
    "data": data,
    "processData": false,
    "contentType": false,
    "mimeType": "multipart/form-data",
    success: function (response) {
      response = JSON.parse(response);
      var status = response['status'];
      var message = response['message'];
      loadDataQrcode();
      alert(message);
    },
    failure: function (error) {

    }
  });
}

