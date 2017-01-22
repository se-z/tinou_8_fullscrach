/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
document.addEventListener('DOMContentLoaded',addEvents,false);//ドキュメントロード時のイベントを追加

var scale = new Array();//座標空間の大きさ
var init_anim_data = new Array();//アニメーション初期状態データ 送信
//var init_data = new Array();//初期状態データ
// var current_data = new Array();//状態データ
//var target_data = new Array();//目標状態データ 使用しない
var process_array = new Array();//プロセスデータ 受診したjsonを格納
var anim_process_array = new Array();//穴掘り込みのプロセスデータ
//var todo_array = new Array();
// var vis_todo_array = new Array();//表示用のプロセスリスト
// var doing_array = new Array();
// var done_array = new Array();
//var update = {state:false}; // 使用しない
var exec = {step:0,anim_step:0,up:false,hor:false,down:false,first:true,target:null};
var anim_start_ary = new Array();
var anim_end_ary = new Array();

var mId;
var mColor = "red";
var mShape = "box";
var mWeight =false;
// mWeight = new Boolean(false);
var mNameList = new Array();

var mYState;
var mXstate;
var mAreaTop = -300;
var mAreaLong = 5; //使用できるx座標空間の定義
//var mBoxAddCount = 0;
var mPanelAddCount = 0;
var mBoxCount = 0;
var mXSpace = 0;

var order_data = new Array(); // 送信する目標状態
var mcount_hole=1; // 穴の数

// Shapeラジオボタンのコントローラー
function ShapeCtrl($scope) {
  $scope.shape = 'box';
}

// Colorラジオボタンのコントローラー
function ColorCtrl($scope) {
  $scope.color = 'red';
}

// Weightチェックボタンのコントローラー
function WeightCtrl($scope) {
  $scope.weight = false;
}

// TargetStateのコントローラー
function TargetCtrl($scope) {

  $scope.CheckTarget = function() {
    // テキストボックスに入力されたものを取り出す
    var tTarget = targetstate.TextArea.value;

    // 入力がある場合は適用ボタンを使用可能にする
    if(tTarget != ""){
      document.getElementById('ApplicateButton').disabled = false;//適用を可能にする
    }else {
      document.getElementById('ApplicateButton').disabled = true;//適用を不可能にする
    }
  };
}

// MakeNameInputのコントローラー
function NameCtrl($scope) {
  $scope.samename = false;

  $scope.CheckName = function() {
    // BoxNameテキストボックスに入力されたものを取り出す
    var tNewName = BoxName.Text.value.toLowerCase();

    // 存在しない名前のとき
    if(jQuery.inArray(tNewName,mNameList)<0){
      $scope.samename = false;
      // 名前が一文字なら
      if(tNewName.length == 1 && tNewName.match("^[0-9a-zA-Z]+$")){
        document.getElementById('SetBoxNameButton').disabled = false;//名前設定を可能にする
      } else {// そうでないなら
        document.getElementById('SetBoxNameButton').disabled = true;//名前設定を不可能にする
      }
    }else{ // 既に存在する名前のとき
      $scope.samename = true;
      document.getElementById('SetBoxNameButton').disabled = false;//名前設定を可能にする
    }
  };
}

// (名前)設定ボタン対応
function onSetBoxNameButton(){
  var tTargetName = document.getElementById("PreviewName");

  // 入力された名前を取得
  mId = BoxName.Text.value;

  // プレビュー用ボックスの対象項目を変更
  tTargetName.textContent = mId;

  // 新たな入力を不可能にする
  document.getElementById('MakeNameInput').disabled = true;//入力を不可能にする
  document.getElementById('SetBoxNameButton').disabled = true;//名前の決定を不可能にする

  var $scope = angular.element('#MakeNameInput').scope();
  // 既に存在するボックスの場合
  if ($scope.samename) {
    document.getElementById('ChangeBoxButton').disabled = false;//変更を可能にする
    SetCurrentBoxState(); // 既存ボックスの状態呼出し
  }else{ // 新しいボックスの場合
    document.getElementById('AddBoxButton').disabled = false;//追加を可能にする
    SetInitBoxState(); // 新規ボックスの状態呼出し
  }
}

// MakeBoxAreaへの新規ボックスの状態設定
function SetInitBoxState(){
  var tTargetBox = document.getElementById("PreviewBox");

  mColor = "red";
  mShape = "box";
  mWeight = false;

  tTargetBox.className = mShape;
  tTargetBox.style.backgroundColor = mColor;
  tTargetBox.style.borderBottom = '';
  $("#PreviewBox").removeClass("heavy");

  // チェックボックスの初期値をbox,red,nocheckedに設定
  RadioShape1.checked=true;
  RadioColor1.checked=true;
  CheckWeight.checked=false;
}

// MakeBoxAreaへの既存ボックスの状態設定
function SetCurrentBoxState(){
  var tTargetBox = document.getElementById("PreviewBox");
  var tNum = mNameList.indexOf(mId);

  mShape = init_anim_data[tNum].shape;
  mWeight = init_anim_data[tNum].heavy;
  mColor = init_anim_data[tNum].color;

  tTargetBox.className = mShape;
  tTargetBox.style.backgroundColor = mColor;

  // boxはbackground、それ以外はborderにcolorを適用
  if(mShape != 'box'){
    tTargetBox.style.backgroundColor = '';
    tTargetBox.style.borderBottom = '100px solid '+ mColor;
  }else{
    tTargetBox.style.backgroundColor = mColor;
    tTargetBox.style.borderBottom = '1px solid '+'#000000';
  }

  // PreviewBoxへのheavyクラスの設定
  if(init_anim_data[tNum].heavy){
    $("#PreviewBox").addClass("heavy")
  } else {
    $("#PreviewBox").removeClass("heavy");
  }

  // チェックボックスを既定値に設定
  if(mShape == 'box'){
    RadioShape1.checked=true;
  }else if(mShape == 'triangle'){
    RadioShape2.checked=true;
  }else{
    RadioShape3.checked=true;
  }

  if(mColor == 'red'){
    RadioColor1.checked=true;
  }else if(mColor == 'green'){
    RadioColor2.checked=true;
  }else if(mColor == 'blue'){
    RadioColor3.checked=true;
  } else {
    RadioColor4.checked=true;
  }

  if(mWeight){
    CheckWeight.checked=true;
  } else {
    CheckWeight.checked=false;
  }

  // Shapeをtriangleにできない状況(頂点じゃない)ならtrue
  if(ApaxCheck(tNum)){
  }else{
  RadioShape2.disabled = true;
  }
}

// ボックスが同X座標で頂点にあるかの判定
function ApaxCheck(aNum){
  var tNum=aNum;
  var tNumY = init_anim_data[tNum].coordinate[0];
  var tNumX = init_anim_data[tNum].coordinate[1];
  ///var tSameXCount = 0;

  for(var i = 0, len = init_anim_data.length; i < len; ++i){
    if(init_anim_data[i].coordinate[1] == tNumX){
      ///tSameXCount++;
      if(/*i != tNum && */init_anim_data[i].coordinate[0] > tNumY){
        return false;///
      }
    }
  }
//  if(tSameXCount == 1){
    return true;
  // }else{
  //   return false;
  // }
}

// MakeBoxAreaの状態初期化
function PrepareMakeBox(){
  document.getElementById('MakeNameInput').disabled = false;//入力を可能にする
  document.getElementById('SetBoxNameButton').disabled = true;//名前の決定を可能にする
  document.getElementById('AddBoxButton').disabled = true;//追加を不可能にする
  document.getElementById('ChangeBoxButton').disabled = true;//変更を不可能にする
  BoxName.Text.value ='';
}

// AddBoxButton対応　ボックスの追加
function onAddBoxButton(){
  var element = document.getElementById("NewBoxPlace");
  var NewBoxs = element.childNodes;

  // 前回作成したブロックが設置されていないなら新規作成できない
  if(NewBoxs.length != 0){
    alert("新規ボックスを初期状態に配置してください");
  }else{
    // ブロック名リストに新しいブロック名を追加
    mNameList.push(mId);

    // 初期状態リストに状態を保存
    var new_init_data = {id:mId,shape:mShape,coordinate:[-1, -1],heavy:mWeight,color:mColor};
    init_anim_data.push(new_init_data);

    // ボックスの作成
    SetData2(mId,[-1,-1]);
    add_box2();

    // // パネルの設置　x座標描画限界(10)まで
    // if(mBoxAddCount<10){
    //   MakePanel();
    //   mBoxAddCount++;
    // }

    mBoxCount++;
    PrepareMakeBox();
  }
}

// パネルの作成
function MakePanel(){
  var div_element = document.createElement("div");
  div_element.setAttribute('class','setpanel canset');//クラス（panel）を指定する
  div_element.setAttribute('id','Panel'+mPanelAddCount.toString());

  var parent_object = document.getElementById("Ground");

  parent_object.appendChild(div_element);
  $('#Panel'+mPanelAddCount.toString()).css({'position':'absolute','left':mPanelAddCount*100,'top':0});

  div_element.addEventListener('dragover',_dragover,false);
  div_element.addEventListener('drop',_drop,false);
  div_element.addEventListener('drop',SetBox,false);
}

// ChangeBoxButton対応　変更の反映
function onChangeBoxButton(){
  var element = document.getElementById("NewBoxPlace");
  var NewBoxs = element.childNodes;

  // 前回作成したブロックが設置されていないなら新規作成できない
  if(NewBoxs.length != 0){
    alert("新規ボックスを初期状態に配置してください");
  }else{

  var tNum = mNameList.indexOf(mId);
  init_anim_data[tNum].shape = mShape;
  init_anim_data[tNum].heavy = mWeight;
  init_anim_data[tNum].color = mColor;

  var tTargetBox = $('#box'+mId);
  var tBoxId = document.getElementById('box'+mId);
  tBoxId.className = "";
  tBoxId.className = mShape;

  if(mShape != 'box'){
    tBoxId.style.backgroundColor = '';
    tBoxId.style.borderBottom = '100px solid '+ mColor;
  }else{
    tBoxId.style.backgroundColor = mColor;
    tBoxId.style.borderBottom = '1px solid '+'#000000';
  }

  if(mWeight==true){
    tTargetBox.addClass("heavy");
  }

  var tPanelId = 'Panel'+init_anim_data[tNum].coordinate[1];

  if(ApaxCheck(tNum)){// 頂点のボックスなら直上のパネルが使えるか判定
    UsePanelCheck(tPanelId,tNum);
  } else {
  }
  PrepareMakeBox();
  RadioShape2.disabled=false;
}
}

// Shapeのラジオボタンに対応
function onRadioButtonChangeShape() {
  // 変更するボックスのIDを指定
  var tTargetBox = document.getElementById("PreviewBox");

  // BoxShapeラジオボックスのcheckedであるもののvalueを取り出す
  mShape = BoxShape.Radio.value;

  // プレビュー用ボックスの対象項目を変更
  tTargetBox.className = mShape;

  // 色の変更を保持(表示用)
  if(mShape != 'box'){
    tTargetBox.style.backgroundColor = '#f8f8ff';
    tTargetBox.style.borderBottom = '100px solid '+ mColor;
  }else{
    tTargetBox.style.backgroundColor = mColor;
    tTargetBox.style.borderBottom = '1px solid '+'#000000';
  }
}

// Colorのラジオボックスに対応
function onRadioButtonChangeColor() {
  // 変更するボックスのIDを指定
  var tTargetBox = document.getElementById("PreviewBox");

  // BoxColorラジオボックスでcheckedであるもののvalueを取り出す
  mColor = BoxColor.Radio.value;

  // プレビュー用ボックスの対象項目を変更
  if(tTargetBox.className != 'box'){
    tTargetBox.style.borderBottom = '100px solid '+ mColor;
  } else {
    tTargetBox.style.backgroundColor = mColor;
    tTargetBox.style.borderBottom = '';
  }
}

// Weightのチェックボタンに対応
function onCheckButtonChangeWeight() {
  // 変更するボックスのIDを指定
  var tTargetBox = document.getElementById("PreviewBox");

  // BoxWeightチェックボックスがチェックされているならtrue、ないならfalseを取り出す
  mWeight = BoxWeight.Check.checked;

  // プレビュー用ボックスの対象項目を変更
  if(CheckWeight.checked){
    $("#PreviewBox").addClass("heavy");
  } else {
    $("#PreviewBox").removeClass("heavy");
  }
}

// ドキュメントロード時に発火
function addEvents(){
  add_Button();
  document.getElementById('MakeNameInput').disabled = false;//入力を可能にする
  document.getElementById('SetBoxNameButton').disabled = true;//名前設定を不可能にする
  document.getElementById('AddBoxButton').disabled = true;//追加を不可能にする
  document.getElementById('ChangeBoxButton').disabled = true;//変更を不可能にする
  document.getElementById('ApplicateButton').disabled = true;//適用を不可能にする
  document.getElementById('ExecButton').disabled = true;//実行を不可能にする
  document.getElementById('TargetInput').disabled = false; // 目標状態の変更を可能にする

  // パネルの設置　x座標描画限界(mAreaLong)まで
  for(var i=0,len=mAreaLong;i<len;++i){
    MakePanel();
    mPanelAddCount++;
  }
}



// 適用ボタン 対応
function onApplicateButton(){
  var tCheckState = false; // 実行可能かどうかの判定
  var element = document.getElementById("NewBoxPlace");
  var NewBoxs = element.childNodes;

  if(mBoxCount<2){
    alert("ボックスを二つ以上作成してください");
    tCheckState = true;
  }else if(NewBoxs.length != 0){
    alert("新規ボックスを初期状態に配置してください");
    tCheckState = true;
  } else{
    // パネルの無効化
    for(var i=0,len = mAreaLong;i<len;++i){
      var tPanelId = 'Panel'+i;
      RemoveDropEvent(tPanelId);
      $('#'+tPanelId.toString()).removeClass("cannotset");
    }
    // ボックスのドラッグ無効化
    for(var j=0,len = mNameList.length;j<len;++j){
      var tBoxId = document.getElementById('box'+init_anim_data[j].id);
      tBoxId.draggable = false;
      tBoxId.removeEventListener('dragstart',_dragstart,false);
    }
    PrepareMakeBox(); // MakeBoxAreaを初期状態にする
    document.getElementById('MakeNameInput').disabled = true; // 名前の入力を不可能にする
    document.getElementById('TargetInput').disabled = true; // 目標状態の変更を不可能にする
    document.getElementById('ApplicateButton').disabled = true; // 適用を不可能にする
  }
  document.getElementById('ExecButton').disabled = tCheckState; // 推論可能ならば実行ボタンを使用可能にする
}

// 実行ボタン対応
function onExecButton(){
  if(exec.step == 0){
    PrepareAjax();
    // ここで、process_arrayにresponse.jsonと同じ形式のデータを格納すればアニメーションを描画できます

  for(var i=0;i<process_array.length;i++){
    var temp = process_array[i].newPosition[0];
    process_array[i].newPosition[0] = process_array[i].newPosition[1];
    process_array[i].newPosition[1] = temp;
  }
    make_hole();
    init_Animation();
    alert("アニメーションを開始します");
  }
  anim_start_ary = new Array(anim_process_array.length);
  anim_end_ary = new Array(anim_process_array.length);
  anim_start_ary.fill(false,0,anim_start_ary.length);
  anim_end_ary.fill(false,0,anim_end_ary.length);
  OperationProcess();//アニメーションを描く
  anim_start_ary[exec.step] = true;
  exec.anim_step = 0;exec.up = false;exec.hor = false;exec.down = false;

  if(exec.step < anim_process_array.length){//プロセスが残っている場合
    exec.step++;//次のプロセスへ
    setTimeout(onExecButton,3000);//自分を時間差でまた呼び出す
  }else{
    exec.step = 0;
    console.log('Exec finished'+anim_process_array.length);
    end_Animation();
  }
}


//英語を日本語に(形)
function transShapeJ(shapeJ){
  shapeJ = shapeJ.replace("box","四角");
  shapeJ = shapeJ.replace("triangle","三角");
  shapeJ = shapeJ.replace("trapezoid","台形");
  return shapeJ;
}

//英語を日本語に(色)
function transColorJ(colorJ){
  colorJ = colorJ.replace("red","赤");
  colorJ = colorJ.replace("blue","青");
  colorJ = colorJ.replace("green","緑");
  colorJ = colorJ.replace("yellow","黄色");
  return colorJ;
}


//GUI上の初期状態、目標状態からファイルを作る
// $('#ExecButton').on('click', function () {
function PrepareAjax(){
  scale=[mAreaLong,4]; //  Y座標は常に4まで
  var scaleX = [0,mAreaLong];
  var scaleY = [-2,4];
  var blocks = new Array();
  var shapeJ = "";
  var colorJ = "";
  for(var i=0; i<init_anim_data.length; i++){
    shapeJ = transShapeJ(init_anim_data[i].shape);
    colorJ = transColorJ(init_anim_data[i].color);
    var new_coordinate = new Array();
    new_coordinate[0] = init_anim_data[i].coordinate[1];
    new_coordinate[1] = init_anim_data[i].coordinate[0];
    var block = {"id":init_anim_data[i].id, "shape":shapeJ, "coordinate":new_coordinate,"heavy":init_anim_data[i].heavy,"color":colorJ};
    blocks[i] = block;
  }
  var str = targetstate.TextArea.value;
  var str2 = new Array();
  while(order_data.pop());
  str2 = str.split("\n");
  for(var j=0;j<str2.length;j++){
    if(str2[j] == ""){
    }else{
      order_data.push(str2[j]);
    }
  }
  var obj = {"X":scaleX,"Y":scaleY,"blocks":blocks,"order":order_data};
  var JSONdata = JSON.stringify(obj,undefined,1);
  console.log(JSONdata);

    $.ajax({
      type: "POST",
      url: "run/",
      dataType: "json",
      contentType:"application/json",
      cache: false,
      async: false,
      data: JSONdata,
    }).done(function (data) {
      console.log("done");
      process_array = data;
//      onExecButton();
      //insertItems(data);
    }).fail(function (jqXHR, statusText, errorThrown) {
      console.log('FAIL!');
    }).always(function () {
      console.log("always");
    });
// });
}



function make_hole(){
  while(anim_process_array.pop());
  mcount_hole = 1;
  var hole_mem = new Array();
  //hole_mem.push([0,0]);
  for(var i=0;i<process_array.length;i++){
    var hole_exist = false;
    var hole_exist2 = false;
    if(process_array[i].newPosition[0] == -2){
      var hole_depth1 = {newPosition:[-1,process_array[i].newPosition[1]]};
      for(var j=0;j<hole_mem.length;j++){
        if(process_array[i].newPosition[0]==hole_mem[j][0]&&process_array[i].newPosition[1]==hole_mem[j][1]){
          hole_exist = true;
        }
        if(hole_depth1.newPosition[0] == hole_mem[j][0]&&hole_depth1.newPosition[1] == hole_mem[j][1]){
          hole_exist2 = true;
        }
      }
      if(hole_exist==false&&hole_exist2==false){
        var hole = {id:"hole"+String(mcount_hole),newPosition:[-1,process_array[i].newPosition[1]]};
        hole_mem.push([-1,process_array[i].newPosition[1]]);
        anim_process_array.push(hole);
        mcount_hole++;
        var anim_temp = {id:process_array[i].id,newPosition:process_array[i].newPosition};
        var hole = {id:"hole"+String(mcount_hole),newPosition:process_array[i].newPosition};
        hole_mem.push(process_array[i].newPosition);
        anim_process_array.push(hole);
        anim_process_array.push(anim_temp);
        mcount_hole++;
      }else if(hole_exist2 == true&&hole_exist == false){
        var hole = {id:"hole"+String(mcount_hole),newPosition:process_array[i].newPosition};
        mcount_hole++;
        var anim_temp = {id:process_array[i].id,newPosition:process_array[i].newPosition};
        hole_mem.push(process_array[i].newPosition);
        anim_process_array.push(hole);
        anim_process_array.push(anim_temp);
      }else{
        var anim_temp = {id:process_array[i].id,newPosition:process_array[i].newPosition};
        anim_process_array.push(anim_temp);
      }
      hole_exist=false;
      hole_exist2=false;

    }else if(process_array[i].newPosition[0] == -1){
      for(var j=0;j<hole_mem.length;j++){
        if(process_array[i].newPosition[0]==hole_mem[j][0]&&process_array[i].newPosition[1]==hole_mem[j][1]){
          hole_exist=true;
        }
      }
      if(hole_exist == false){
        var hole = {id:"hole"+String(mcount_hole),newPosition:process_array[i].newPosition};
        mcount_hole++;
        hole_mem.push(process_array[i].newPosition);
        anim_process_array.push(hole);
      }
      var anim_temp = {id:process_array[i].id,newPosition:process_array[i].newPosition};
      anim_process_array.push(anim_temp);
      hole_exist = false;
    }else{
      var anim_temp  = {id:process_array[i].id,newPosition:process_array[i].newPosition};
      //anim_process_array[anim_length].id = process_array[i].id;
      //anim_process_array[anim_length].newPosition = process_array[i].newPosition;
      anim_process_array.push(anim_temp);
    }
  }
  console.log(hole_mem);
  console.log(anim_process_array);
}



// ボタンに関数を付与する
function add_Button(){
  var ApplicateButton = document.getElementById('ApplicateButton');
  ApplicateButton.addEventListener('click',onApplicateButton,false);

  var ExecButton = document.getElementById('ExecButton');
  ExecButton.addEventListener('click',onExecButton,false);

  var NameButton = document.getElementById('SetBoxNameButton');
  NameButton.addEventListener('click',onSetBoxNameButton,false);

  var RadioShape1 = document.getElementById('RadioShape1');
  RadioShape1.addEventListener('change',onRadioButtonChangeShape,false);

  var RadioShape2 = document.getElementById('RadioShape2');
  RadioShape2.addEventListener('change',onRadioButtonChangeShape,false);

  var RadioShape3 = document.getElementById('RadioShape3');
  RadioShape3.addEventListener('change',onRadioButtonChangeShape,false);

  var RadioColor1 = document.getElementById('RadioColor1');
  RadioColor1.addEventListener('change',onRadioButtonChangeColor,false);

  var RadioColor2 = document.getElementById('RadioColor2');
  RadioColor2.addEventListener('change',onRadioButtonChangeColor,false);

  var RadioColor3 = document.getElementById('RadioColor3');
  RadioColor3.addEventListener('change',onRadioButtonChangeColor,false);

  var RadioColor4 = document.getElementById('RadioColor4');
  RadioColor4.addEventListener('change',onRadioButtonChangeColor,false);

  var CheckWeight = document.getElementById('CheckWeight');
  CheckWeight.addEventListener('change',onCheckButtonChangeWeight,false);

  var AddBoxButton = document.getElementById('AddBoxButton');
  AddBoxButton.addEventListener('click',onAddBoxButton,false);

  var ChangeBoxButton = document.getElementById('ChangeBoxButton');
  ChangeBoxButton.addEventListener('click',onChangeBoxButton,false);
}



function init_Animation(){
  add_arm();
}

// アニメーション終了処理
function end_Animation(){
alert("アニメーションを終了します");
    // すべてのパネルの有効化
    for(var i=0,len = mAreaLong;i<len;++i){
      var tPanelId = 'Panel'+i;
      SetDropEvent(tPanelId);
    }
    // ボックスのドラッグ有効化
    for(var j=0,len = mNameList.length;j<len;++j){
      var tBoxId = document.getElementById('box'+init_anim_data[j].id);
      tBoxId.draggable = true;
      tBoxId.addEventListener('dragstart',_dragstart,false);

      var tBoxState = init_anim_data[j].coordinate;//[1];
      var tBoxXState = tBoxState[1];
      var tPanelId = 'Panel'+tBoxXState.toString();
      if(ApaxCheck(j)){ // 頂点のボックスの場合は
        UsePanelCheck(tPanelId,j); // 三角形なら直上のパネルを無効化
      }
    }
    ResetState(); // アニメーションを初期状態へ戻す
    PrepareMakeBox(); // MakeBoxAreaを初期状態にする
    document.getElementById('TargetInput').disabled = false; // 目標状態の変更を可能にする
    document.getElementById('ApplicateButton').disabled = false; // 適用を可能にする
    document.getElementById('ExecButton').disabled = true; // 実行を不可能にする
}

// 指定されたリストのi番目の要素についてID、座標をセットする
function SetData2(aIdData,aStateData){
  console.log("aId="+aIdData+"aStateData"+aStateData);
  mId = aIdData;
  var tStates = aStateData;
  mYState = (-tStates[0])*100;
  mXState = tStates[1]*100;
}



// プロセスの描画
function OperationProcess(){
  if(exec.step < anim_process_array.length){
    // SetData2(process_array[exec.step].Target,process_array[exec.step].New);

    var tBoxId = anim_process_array[exec.step].id;
    var tBoxNewPosition = anim_process_array[exec.step].newPosition;
    var tWeight = $('#box'+tBoxId.toString()).hasClass('heavy');

    // ホール込みのものを参照する
    SetData2(tBoxId,tBoxNewPosition);
    set_arm();
    // アームを動かすブロックにセットするために遅延
    if(tBoxId.toString().indexOf('hole') != -1){
      setTimeout(dig_hole,1500);//穴掘り
    }else if(tWeight == true){
      setTimeout(move_heavy_box,1500);//引きずり
    }else{
      setTimeout(move_box,1500);//通常移動
    }
  }
}


function set_arm(){
  if(mId.length>1){
    $('#arm1')
    .animate({'top':mAreaTop-25},300)
    .animate({'left':mXState},300)
    .animate({'top':mYState-25},300)
  }else{
    var tSetArm = $('#box'+mId).position();
    $('#arm1')
    .animate({'top':mAreaTop-25},300)
    .animate({'left':tSetArm.left},300)
    .animate({'top':tSetArm.top-25},300)
  }
}


// 描画状態のリセット
function ResetState() {
  // ブロックを初期状態に移動させる
  for (var i = 0; i < init_anim_data.length; ++i) {
    SetData2(init_anim_data[i].id,init_anim_data[i].coordinate);
    ///mYState = mYState*(-1);
    move_box2();
  }
  _delete_element('arm1'); // アームの消去

  // 穴(ホール)をすべて消去 (init_anim_dataではなくホールの数でループさせる)
  for (var i = 1; i < mcount_hole; ++i) {
    _delete_element('hole'+i);
  }

}


// 指定したIDのオブジェクトを削除
function _delete_element(aId){
    var dom_obj = document.getElementById(aId);
    var dom_obj_parent = dom_obj.parentNode;
    dom_obj_parent.removeChild(dom_obj);
}

// アームの作成
function add_arm()
{
  var div_element = document.createElement("div");
  div_element.setAttribute('class','arm');
  div_element.setAttribute('id','arm1');
  var parent_object = document.getElementById("Ground");

  parent_object.appendChild(div_element);
  $('#arm1').css({'position':'absolute','left':0,'top':mAreaTop-25});
}

// 穴掘りアニメーション
function dig_hole(){
  add_hole();
  setTimeout(function(){anim_end_ary[exec.step] = true; },3000);

  $('#arm1').animate({'top':mAreaTop-25},1500);
}

// 穴の作成
function add_hole()
{
  var div_element = document.createElement("div");
   div_element.setAttribute('class','hole');
  div_element.setAttribute('id',mId.toString());

  var parent_object = document.getElementById("Ground");

  parent_object.appendChild(div_element);
  $('#'+mId.toString()).css({'position':'absolute','left':mXState,'top':mYState});
}



function add_box2(){
  var div_element = document.createElement("div");
  div_element.setAttribute('class',mShape);//クラス（図形）を指定する
  if(mShape != 'box'){
    div_element.style.borderBottom = '100px solid '+mColor;
  }
  else{
    div_element.style.background = mColor;
  }
  div_element.setAttribute('id','box'+mId.toString());
  var word_element = document.createElement("div");
  word_element.setAttribute('class','word');
  var word = document.createElement('p');
  word.textContent = mId;
  word_element.appendChild(word);
  div_element.appendChild(word_element);
  //  var parent_object = document.getElementById("Anim");

  // NewBoxPlaceにへんこう
  var parent_object = document.getElementById("NewBoxPlace");

  parent_object.appendChild(div_element);
  $('#box'+mId.toString()).css({'position':'absolute','left':mXState,'top':mYState});

  if(mWeight==true){
    $('#box'+mId.toString()).addClass("heavy");
  }
  div_element.draggable = true;
  div_element.addEventListener('dragstart',_dragstart,false);
}


// ボックスの移動 実行用
function move_box(){
  setTimeout(function(){anim_end_ary[exec.step] = true; },3000);
  $('#arm1')
  .animate({'top':mAreaTop-25},500)
  .animate({'left':mXState},500)
  .animate({'top':mYState-25},500)
  $('#box'+mId)
  .animate({'top':mAreaTop},500)
  .animate({'left':mXState},500)
  .animate({'top':mYState},500)
}

// 重いボックスの移動 実行用
function move_heavy_box(){
  setTimeout(function(){anim_end_ary[exec.step] = true; },3000);
  $('#arm1')
  .animate({'top':-25},500)
  .animate({'left':mXState},500)
  .animate({'top':mYState-25},500)
  $('#box'+mId)
  .animate({'top':0},500)
  .animate({'left':mXState},500)
  .animate({'top':mYState},500)
}


//ボックスの移動　やり直し用
function move_box2(){
  $('#box'+mId)//id を指定する
  .animate({'top':mAreaTop},500)
  .animate({'left':mXState},500)
  .animate({'top':mYState},500)
}



// ドラッグが始まった時の処理
function _dragstart(event){
  event.dataTransfer.setData("text", event.target.id);
}

// ドラッグしている要素がdropboxゾーンと重なっているときの処理
function _dragover(event){
  event.preventDefault();
}

// 描画領域へのドロップ時の処理 Groundの子要素に適応
function _drop(event){
  var id_name = event.dataTransfer.getData("text");
  var drag_elm =document.getElementById(id_name);
  // event.currentTarget.appendChild(drag_elm);
  // docuent.Ground.appendChild(drag_elm);

  // ドロップされたボックスをGroundの子要素に設定
  var parent_object = document.getElementById("Ground");
  parent_object.appendChild(drag_elm);

  event.preventDefault();
  // RadioShape2.disabled=false;
}

// DeleteBoxArea(ゴミ箱)領域へのドロップ時の処理
function _drop_delete(event){
  var id_name = event.dataTransfer.getData("text");
  var drag_elm =document.getElementById(id_name);
  // event.currentTarget.appendChild(drag_elm);
  // docuent.Ground.appendChild(drag_elm);

  // 削除したボックスが元いたX座標の状況整理
  var tIdScript = id_name.replace( /box/g , "" );
  var tNum = mNameList.indexOf(tIdScript);
  DownBoxs(tNum);

  // 各リストから削除したボックスの情報を消去
  init_anim_data.splice(tNum,1);
  mNameList.splice(tNum,1);
  mBoxCount--;

  // ボックスオブジェクトの消去
  _delete_element(id_name);

  event.preventDefault();
  // RadioShape2.disabled=false;
}

// 描画領域へのドロップ時の処理 Ground内での処理
function SetBox(event){
  var id_name = event.dataTransfer.getData("text");
  var tPanelId = event.currentTarget.id;
  var tPanelPosition = $('#'+tPanelId).position();
  var tIdScript = id_name.replace( /box/g , "" ) ;
  var tNum = mNameList.indexOf(tIdScript);

  // 横移動の場合のみ移動を認める
  if(SideCheck(tPanelId,tNum)){
    // 移動したオブジェクトの座標情報を変更
    mXState = Math.ceil(tPanelPosition.left);
    mYState = Math.ceil(tPanelPosition.top);
    init_anim_data[tNum].coordinate[0] = -mYState/100;////
    init_anim_data[tNum].coordinate[1] = mXState/100;
    $('#'+id_name).css({'left':mXState,'top':mYState});

    // 移動後のX座標のパネルが使用できるか判定
    UsePanelCheck(tPanelId,tNum);

    // パネルのY座標を一つ上に変更
    var tNewYState = tPanelPosition.top-100;
    $('#'+tPanelId).css({'top':tNewYState});
  }
}

// ボックスが設置されたパネルと動かしたブロックの元の位置から横移動かどうかを判定
function SideCheck(aPanelId,aNum) {
  var tPanelId = aPanelId; // 設置されたパネル
  var tNum = aNum; // ブロック
  var tPanelIdScript = tPanelId.replace( /Panel/g , "" ) ;

  if(tPanelIdScript != init_anim_data[tNum].coordinate[1]){
    if(-1 != init_anim_data[tNum].coordinate[1]){
      DownBoxs(aNum); // 初期移動じゃないなら元いたx成分要素の位置を修正
    }
    return true; // 横移動なら
  } else {
    return false; // 縦移動なら
  }
}

// 移動したボックスのx成分要素の位置修正
function DownBoxs(aNum){
  var tNum=aNum;
  var tNumY = init_anim_data[tNum].coordinate[0];
  var tNumX = init_anim_data[tNum].coordinate[1];
  var j = new Array();

  // 同X座標のボックスの位置修正
  for(var i = 0, len = init_anim_data.length; i < len; ++i){
    if(init_anim_data[i].coordinate[1] == tNumX){
      if(/*i != tNum &&*/ init_anim_data[i].coordinate[0] > tNumY){///
        j.push(i);
        init_anim_data[i].coordinate[0] -= 1;///
        var NewY = init_anim_data[i].coordinate[0]*(-100);/////
        $('#box'+init_anim_data[i].id.toString()).css({'top':NewY});
      }
    }
  }

  if(tNumX.toString()>=0){  // 初期配置でないとき
    var tNewPanelY = $('#Panel'+tNumX.toString()).position().top;
    if(tNewPanelY < 0){////
      tNewPanelY +=100; // パネルの位置を一つ下げる////
    }
    $('#Panel'+tNumX.toString()).css({'top':tNewPanelY});

    // 三角形でなければパネルへのドロップを可能にする
    var canset = true;
    for(var i = 0, len = j.length; i < len; ++i){
      var tXBoxNumber = j[i];
      if(init_anim_data[tXBoxNumber].shape == "triangle"){
        canset = false;
      }
    }

    if(canset == true){
      SetDropEvent('Panel'+tNumX);
    }
  }
}

// パネルとその下のボックスの番号を渡してパネルが使えるかを判断
function UsePanelCheck(aPanelId,aNum){
  var tPanelId = aPanelId;
  var tPanelPosition = $('#'+tPanelId).position();
  var tNum = aNum;

  // ボックスが三角形か、Y座標上限でないかを元に判定
  if(init_anim_data[tNum].shape == "triangle" || init_anim_data[tNum].coordinate[0]*(-100) == mAreaTop){///
    RemoveDropEvent(tPanelId); // パネルへのドロップを禁止
  } else {
    SetDropEvent(tPanelId);// パネルへのドロップを可能にする
  }
}

// パネルへのドロップを禁止する
function RemoveDropEvent(aPanelId){
  var tPanelId = document.getElementById(aPanelId);
  tPanelId.removeEventListener('dragover',_dragover,false);
  tPanelId.removeEventListener('drop',_drop,false);
  tPanelId.removeEventListener('drop',SetBox,false);

  $('#'+aPanelId.toString()).addClass("cannotset");
  $('#'+aPanelId.toString()).removeClass("canset");
}

// パネルへのドロップを可能にする
function SetDropEvent(aPanelId){
  var tPanelId = document.getElementById(aPanelId);
  tPanelId.addEventListener('dragover',_dragover,false);
  tPanelId.addEventListener('drop',_drop,false);
  tPanelId.addEventListener('drop',SetBox,false);

  $('#'+aPanelId.toString()).addClass("canset");
  $('#'+aPanelId.toString()).removeClass("cannotset");
}