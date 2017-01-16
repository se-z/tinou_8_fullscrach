/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
document.addEventListener('DOMContentLoaded',addEvents,false);//ドキュメントロード時のイベントを追加

var scale = new Array();//座標空間の大きさ
var init_anim_data = new Array();//アニメーション初期状態データ 送信
var init_data = new Array();//初期状態データ
// var current_data = new Array();//状態データ
var target_data = new Array();//目標状態データ 使用しない
var process_array = new Array();//プロセスデータ 受診したjsonを格納
var anim_process_array = new array();//穴掘り込みのプロセスデータ
var todo_array = new Array();
// var vis_todo_array = new Array();//表示用のプロセスリスト
// var doing_array = new Array();
// var done_array = new Array();
var update = {state:false}; // 使用しない
var exec = {step:0,anim_step:0,up:false,hor:false,down:false,first:true,target:null};
var anim_start_ary = new Array();
var anim_end_ary = new Array();

var mId;
var mColor = "#ff0000";
var mShape = "box";
var mWeight =false;
// mWeight = new Boolean(false);
var mNameList = new Array();

var mYState;
var mXstate;
var mAreaTop = -300;

var mBoxCount = 0;
var mXSpace = 0;

var order_date = new Array(); // 送信する目標状態

function ShapeCtrl($scope) {
  $scope.shape = 'box';
}

function ColorCtrl($scope) {
  $scope.color = 'red';
}

function WeightCtrl($scope) {
  $scope.weight = false;
}

function NameCtrl($scope) {
  $scope.samename = false;

  $scope.CheckName = function() {
    // BoxNameテキストボックスに入力されたものを取り出す
    var tNewName = BoxName.Text.value.toLowerCase();

    // 存在しない名前のとき
    if(jQuery.inArray(tNewName,mNameList)<0){
      $scope.samename = false;
      if(tNewName.length == 1 && tNewName.match("^[0-9a-zA-Z]+$")){// 名前が一文字なら
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

function onSetBoxNameButton(){
  var tTargetName = document.getElementById("PreviewName");

  // 入力された名前を取得
  mId = BoxName.Text.value;

  // プレビュー用ボックスの対象項目を変更(表示用)
  tTargetName.textContent = mId;

  // 新たな入力を不可能にする
  document.getElementById('MakeNameInput').disabled = true;//入力を不可能にする
  document.getElementById('SetBoxNameButton').disabled = true;//名前の決定を不可能にする

  var $scope = angular.element('#MakeNameInput').scope();
  // 既に存在するボックスの場合
  if ($scope.samename) {
    document.getElementById('ChangeBoxButton').disabled = false;//変更を可能にする
  }else{ // 新しいボックスの場合
    document.getElementById('AddBoxButton').disabled = false;//追加を可能にする
    SetInitBoxState();
  }
  // alert($('#boxa').parents('div').attr('id'));
}


function SetInitBoxState(){
  var tTargetBox = document.getElementById("PreviewBox");

  mColor = "#ff0000";
  mShape = "box";
  mWeight = false;

  tTargetBox.className = mShape;
  tTargetBox.style.backgroundColor = mColor;
  tTargetBox.style.borderBottom = '';
  $("#PreviewBox").removeClass("heavy");

  RadioShape1.checked=true;
  RadioColor1.checked=true;
  CheckWeight.checked=false;
}


function PrepareMakeBox(){
  document.getElementById('MakeNameInput').disabled = false;//入力を可能にする
  document.getElementById('SetBoxNameButton').disabled = true;//名前の決定を可能にする
  document.getElementById('AddBoxButton').disabled = true;//追加を不可能にする
  document.getElementById('ChangeBoxButton').disabled = true;//変更を不可能にする

  BoxName.Text.value ='';
  // BoxName.Text.value =init_anim_data[0];
}

function onAddBoxButton(){
  // ブロック名リストに新しいブロック名を追加
  mNameList.push(mId);

  var new_init_data = {id:mId,shape:mShape,coodinate:[0, 0],heavy:mWeight,color:mColor};

  // init_anim_data[i].id="";
  // init_anim_data.push(data[i]);

  // 初期状態リストに状態を保存
  init_anim_data.push(new_init_data);

  SetData2(init_anim_data[mBoxCount].id,init_anim_data[mBoxCount].coodinate);
  add_box2();


  if(mBoxCount<10){
    MakePanel();
  }

  mBoxCount++;

  PrepareMakeBox();
}


function MakePanel(){
  var div_element = document.createElement("div");
  div_element.setAttribute('class','setpanel');//クラス（panel）を指定する
  div_element.setAttribute('id','Panel'+mBoxCount.toString());

  var parent_object = document.getElementById("Ground");

  parent_object.appendChild(div_element);
  $('#Panel'+mBoxCount.toString()).css({'position':'absolute','left':mBoxCount*100,'top':0});

  div_element.addEventListener('dragover',_dragover,false);
  div_element.addEventListener('drop',_drop,false);
  div_element.addEventListener('drop',SetBox,false);
  // div_element.addEventListener('drop',AboveCheck,false);
}

function onChangeBoxButton(){
  PrepareMakeBox();
}


function onRadioButtonChangeShape() {
  // 変更するボックスのIDを指定
  var tTargetBox = document.getElementById("PreviewBox");

  // BoxShapeラジオボックスのcheckedであるもののvalueを取り出す
  mShape = BoxShape.Radio.value;

  // プレビュー用ボックスの対象項目を変更(表示用)
  tTargetBox.className = mShape;

  // 色の変更を保持(表示用)
  if(mShape != 'box'){
    tTargetBox.style.backgroundColor = '#f8f8ff';
    tTargetBox.style.borderBottom = '100px solid '+ mColor;
  }else{
    tTargetBox.style.backgroundColor = mColor;
    tTargetBox.Style.borderBottom = '1px solid '+'#000000';
  }
}

function onRadioButtonChangeColor() {
  // 変更するボックスのIDを指定
  var tTargetBox = document.getElementById("PreviewBox");

  // BoxColorラジオボックスでcheckedであるもののvalueを取り出す
  mColor = BoxColor.Radio.value;

  // プレビュー用ボックスの対象項目を変更(表示用)
  if(tTargetBox.className != 'box'){
    tTargetBox.style.borderBottom = '100px solid '+ mColor;
  } else {
    tTargetBox.style.backgroundColor = mColor;
    tTargetBox.style.borderBottom = '';
  }
}


function onCheckButtonChangeWeight() {
  // 変更するボックスのIDを指定
  var tTargetBox = document.getElementById("PreviewBox");

  // BoxWeightチェックボックスがチェックされているならtrue、ないならfalseを取り出す
  mWeight = BoxWeight.Check.checked;

  // プレビュー用ボックスの対象項目を変更(表示用)
  if(CheckWeight.checked){
    $("#PreviewBox").addClass("heavy");
    // tTargetBox.style.boxShadow = '0 15px 5px -4px rgba(0,0,0,0.6)';
  } else {
    $("#PreviewBox").removeClass("heavy");
    // tTargetBox.style.boxShadow = '';
  }
}



function addEvents(){
    add_Button();

    document.getElementById('Exec').disabled = true;//実行を不可能にする

    document.getElementById('MakeNameInput').disabled = false;//入力を可能にする
    document.getElementById('SetBoxNameButton').disabled = true;//名前設定を不可能にする
    document.getElementById('AddBoxButton').disabled = true;//追加を不可能にする
    document.getElementById('ChangeBoxButton').disabled = true;//変更を不可能にする
    document.getElementById('ExecButton').disabled = true;//実行を不可能にする
}


//読み込みイベント 読み込みボタン対応
function loadEvents(){
     document.getElementById('Exec').disabled = false;//実行を可能にする
    //  document.getElementById('Apply').disabled = false;//適用を可能にする
    //  document.getElementById('ReDo').disabled = false;//やり直しを可能にする
     get_init_data();//初期状態データを読み込む
    //  var anim = document.getElementById('Anim');

    // Groundに変更
     var anim = document.getElementById('Ground');

     delete_children(anim);//ブロックが存在すれば全消去
    //  make_StateList('InitState',init_data);
    //  make_StateList('TargetState',target_data);
    //  done_array = new Array();
    //  make_TaskList('Done',done_array);
     update.state = false;
}



// 実行ボタン対応
function onExecButton(){
  if(exec.step == 0){
    //make_ObjFile2();　// 送信データを作成
    //get_process_data2();//過程データを読み込む
    make_hole();
    init_Animation();
  }
  anim_start_ary = new Array(todo_array.length);
  anim_end_ary = new Array(todo_array.length);
  anim_start_ary.fill(false,0,anim_start_ary.length);
  anim_end_ary.fill(false,0,anim_end_ary.length);

  OperationProcess();//アニメーションを描く
  anim_start_ary[exec.step] = true;
  exec.anim_step = 0;exec.up = false;exec.hor = false;exec.down = false;

  if(exec.step < todo_array.length){//プロセスが残っている場合
    exec.step++;//次のプロセスへ
    setTimeout(onExecButton,3000);//自分を時間差でまた呼び出す
  }else{
    exec.step = 0;
    console.log('Exec finished'+todo_array.length);
 }
}

//GUI上の初期状態、目標状態からファイルを作る
$('#ExecButton').on('click', function () {
  var blocks = new Array();
  for(var i=0; i<init_anim_data.length; i++){
    var block = {"id":init_anim_data[i].id, "shape":init_anim_data[i].shape, "coordinate":init_anim_data[i].coordinate,"heavy":init_anim_data[i].heavy,"color":init_anim_data[i].color}
    blocks[i] = block;
  }
  var obj = {"space":scale,"blocks":blocks,"order":order_data}
  var JSONdata = JSON.stringify(obj,undefined,1);


    $.ajax({
      type: "POST",
      url: "/chain/",
      dataType: "json",
      cache: false,
      async: false,       //warningが出るが、ここをfalseにしないと正しく挙動しない
      data: JSONdata,
    }).done(function (data)
      process_array = data;
      onExecButton();
      //insertItems(data);
    }).fail(function (jqXHR, statusText, errorThrown) {
      console.log('FAIL!');
    }).always(function () {
    });
});



function make_hole(){
  var count_hole = 1;
  var hole_mem = new Array();
  //hole_mem.push([0,0]);
  for(var i=0;i<process_array.length;i++){
    var hole_exist = false;
    var hole_exist2 = false;
    if(process_array[i].newPosition[1] == -2){
      var hole_depth1 = {newPosition:[process_array[i].newPosition[0],-1]};
      for(var j=0;j<hole_mem.length;j++){
        if(process_array[i].newPosition[0]==hole_mem[j][0]&&process_array[i].newPosition[1]==hole_mem[j][1]){
          hole_exist = true;
        }
        if(hole_depth1.newPosition[0] == hole_mem[j][0]&&hole_depth1.newPosition[1] == hole_mem[j][1]){
          hole_exist2 = true;
        }
      }
      if(hole_exist==false&&hole_exist2==false){
        var hole = {id:"hole"+String(count_hole),newPosition:[process_array[i].newPosition[0],-1]};
        hole_mem.push([process_array[i].newPosition[0],-1]);
        anim_process_array.push(hole);
        count_hole++;
        var anim_temp = {id:process_array[i].id,newPosition:process_array[i].newPosition};
        var hole = {id:"hole"+String(count_hole),newPosition:process_array[i].newPosition};
        hole_mem.push(process_array[i].newPosition);
        anim_process_array.push(hole);
        anim_process_array.push(anim_temp);
        count_hole++;
      }else if(hole_exist2 == true&&hole_exist == false){
        var hole = {id:"hole"+String(count_hole),newPosition:process_array[i].newPosition};
        count_hole++;
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

    }else if(process_array[i].newPosition[1] == -1){
      for(var j=0;j<hole_mem.length;j++){
        if(process_array[i].newPosition[0]==hole_mem[j][0]&&process_array[i].newPosition[1]==hole_mem[j][1]){
          hole_exist=true;
        }
      }
      if(hole_exist == false){
        var hole = {id:"hole"+String(count_hole),newPosition:process_array[i].newPosition};
        count_hole++;
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






//描画イベント 実行ボタン対応
function execEvents(){
    if(exec.step == 0){
        //ResetState();
        document.getElementById('Exec').disabled = true;//実行を可能にする
        beforeExec();
        make_ObjFile();
        //本来はこの行で Java プログラムを呼び出す
        get_process_data();//過程データを読み込む
        // vis_todo_array = todo_array.concat();//何も加えずに concat することで値だけをコピー（別参照）
        // current_data = init_data.concat();
         init_Animation();
    }
    // doing_ary = new Array();
    // done_ary = new Array();
    //  make_TaskList('ToDo',todo_array);
     anim_start_ary = new Array(todo_array.length);
     anim_end_ary = new Array(todo_array.length);
     anim_start_ary.fill(false,0,anim_start_ary.length);
     anim_end_ary.fill(false,0,anim_end_ary.length);
    //  doing_array.push(vis_todo_array.shift());//先頭を取り出す
    //  make_TaskList('ToDo',vis_todo_array);
    //  make_TaskList('Doing',doing_array);
    //  if(exec.step < todo_array.length){
    //      update_State();
    // }
     OperationProcess();//アニメーションを描く
     anim_start_ary[exec.step] = true;
    //  done_array.push(doing_array.pop());//完了したものを移す
     //make_TaskList('Doing',doing_array);
     //make_TaskList('Done',done_array);
     exec.anim_step = 0;exec.up = false;exec.hor = false;exec.down = false;


    if(exec.step < todo_array.length){//プロセスが残っている場合
        exec.step++;//次のプロセスへ
         setTimeout(execEvents,3000);//自分を時間差でまた呼び出す
    }
    else{
        //  make_TaskList('Doing',doing_array);
        //  done_array.pop();
        //  make_TaskList('Done',done_array);
         exec.step = 0;
         console.log('Exec finished'+todo_array.length);
         //document.getElementById('Exec').disabled = false;//実行を可能にする
    }

}

// function applyEvents(){
    //  var initStateBox = document.getElementById('InitState');
    // var tgtStateBox  = document.getElementById('TargetState');

    // var initStateS = initStateBox.childNodes;
    // var new_init_data = new Array();
    // for(var i = 0, len = initStateS.length; i < len; ++i){
    //     new_init_data.push(initStateS[i].value);
    // }
    // init_data = new_init_data;

    // var tgtStateS = tgtStateBox.childNodes;
    // var new_target_data = new Array();
    // for(var i = 0, len = tgtStateS.length; i < len; ++i){
    //     new_target_data.push(tgtStateS[i].value);
    // }
    // target_data = new_target_data;
//     update.state = false;
//     exec.first = true;
// }

// function redoEvents(){
    // make_StateList('InitState',init_data);
    // make_StateList('TargetState',target_data);
    // ResetState();
    // update.state = false;
    // get_process_data();
    // make_TaskList('ToDo',todo_array);
    // make_TaskList('Doing',doing_array);
    // doing_array = new Array();
    // done_array = new Array();
    // make_TaskList('Done',done_array);
//     document.getElementById('Exec').disabled = false;//実行を可能にする
//     exec.step = 0;
// }

function add_Button(){
    var loadButton = document.getElementById('Load');
    loadButton.addEventListener('click',loadEvents,false);

    var exeButton = document.getElementById('Exec');
    exeButton.addEventListener('click',execEvents,false);


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

function beforeExec(){
    if(update.state){//状態が更新されていたが適用されていない場合
       var result =  window.confirm('変更が適用されていません。\n適用しますか？');
       if(result){//OKボタンが押された場合
           applyEvents();
       }
    }
}

// function update_State(){
//     var current_process = process_array[exec.step];
//     var del_num_ary = new Array();
//     for(var i = 0,len = current_process.diffMinus.length; i < len; ++i){
//         del_num_ary.push(current_data.indexOf(current_process.diffMinus[i]));
//         current_data.splice(del_num_ary[i],1);//削除する
//     }
//     //var del_num = current_data.indexOf(current_process.diffMinus[0]);//今のプロセスの削除対象の添え字を得る
//     console.log("step="+exec.step+" proc="+current_process.toString()+" del="+del_num_ary.toString());
//     for(var i = 0,len = current_process.diffPlus.length; i < len; ++i){
//         current_data.push(current_process.diffPlus[i]);
//     }
//     // make_StateList('InitState',current_data);
// }

//状態リストを作る
function make_StateList(id,data){
    var parent = document.getElementById(id);
    delete_children(parent);
    for(var i =0, len = data.length; i < len; ++i){
        var obj = document.createElement('input');
        obj.setAttribute('data-origin',data[i]);
        obj.setAttribute('value',data[i]);
        obj.setAttribute('type','text');
        obj.setAttribute('class','MiniState');
        obj.addEventListener('keydown',keydown_StateList,false);
        parent.appendChild(obj);
    }
}

function keydown_StateList(e){
    //var text =  e.target.value;//現在の文字フォーム内の文字を入手する
    //console.log('you edited '+text);
    var key = e.keyCode;//押されたキーのコードを入手する
    //console.log('you pushed '+key);
    switch(key){
        case 13:{//13(Enter) が押された場合
                var obj = document.createElement('input');//新しい要素を作る
                obj.setAttribute('type','text');
                obj.setAttribute('class', 'MiniState');//属性を設定する
                obj.addEventListener('keydown',keydown_StateList,false);//イベントを追加する
                var next_Node = e.target.nextSibling;//使用中のノードの次のノードを入手する
                e.target.parentNode.insertBefore(obj,next_Node);//ノードを追加する
                obj.focus();//カーソルを移動
                break;
        }
        case 8:{//8(BackSpace) が押された場合
                var tgt = e.target;
                var text_len = tgt.value.length;//文章の長さを入手する
                var ownsNum = tgt.parentNode.childNodes.length;//自分を含めた仲間のテキストボックスの数を求める
                if(text_len == 0 && ownsNum > 1){//文章がゼロの状態で更に消そうとしていて、このテキストボックスを消してもまたテキストボックスが残る場合
                    var prev = tgt.previousSibling;
                    var next = tgt.nextSibling;
                   tgt.parentNode.removeChild(tgt);//ノードを削除する
                    if(next != null){
                        next.focus();
                    }
                    else if(prev != null){
                        prev.focus();
                    }
                }
                break;
        }
        case 38:{//38(上矢印) が押された場合
                var prev_Node = e.target.previousSibling;//使用中のノードの前のノードを入手する
                if(prev_Node != null){//前のノードが存在した場合
                    prev_Node.focus();//カーソルを移動
                }
                break;
        }
        case 40:{//40(下矢印) が押された場合
                var next_Node = e.target.nextSibling;//使用中のノードの次のノードを入手する
                if(next_Node != null){//次のノードが存在した場合
                    next_Node.focus();//カーソルを移動
                }
                break;
        }
    }

  var origin_value = e.target.getAttribute('data-origin');
  var value = e.target.value;
  if(value != origin_value){
   update.state = true;
  }

}


function make_TaskList(id,array){
    var parent = document.getElementById(id);
    delete_children(parent);
    for(var i =0, len = array.length; i < len; ++i){
        var obj = document.createElement('p');
        obj.textContent = array[i];
        obj.setAttribute('class','MiniTask');
        //obj.addEventListener('keydown',keydown_StateList,false);
        parent.appendChild(obj);
    }
}

//parent の子ノード children を全て削除する
function delete_children(parent){
    while(parent.firstChild){//子供がいる限り続ける
        var child = parent.firstChild;
        parent.removeChild(child);//子供を削除する
    }
}

//初期状態データを入手する
function get_init_data(){
    while(init_anim_data.pop());
    while(init_data.pop());
    while(target_data.pop());

    var data = readJSON('InitTest.json');//JSONデータを入手する
    for(var i = 0; i < data.length; ++i){
        if(i == 0){
            scale = data[i].label;
        }
        else if(i == (data.length -2)){//最後から２つめの場合
            init_data = data[i].label;
        }
        else if(i == (data.length - 1) ){//最後の場合
            target_data = data[i].label;
        }
        else{
            init_anim_data.push(data[i]);
        }
    }
}

function get_process_data(){
    todo_array = new Array();
    process_array = readJSON('ProcessTest.json');//JSONデータを入手する
    for(var i =0,len = process_array.length; i < len; ++i){
        todo_array.push(process_array[i].Motion);
    }

    //console.log(process_array[0].Motion);
}

//ローカルの JSON　ファイルを読み出しオブジェクトにして返す
function readJSON(file_name){
    var nodes;
    //ローカルファイルを読み込む(ブラウザ依存あり Firefoxでしかまともに動かない)
    $.ajax({
        //url : '../Data/InitTest.json',//所在地(相対パス) Playなしでは動かない
        url : './Data2/'+file_name,//所在地(相対パス) Playなしでもスクリプト直下のディレクトリなら読み込める
        dataType: 'json',//ファイルのタイプ JSON形式で読み込む
        cache:false,
        async:false
    }).done(function(data){
        nodes = data;//nodes に入れるこの時既に JSON 型
    });

    //console.log("nodes= "+nodes[0]);
    return(nodes);
}

function saveJSON(){
    $.ajax({
        type: "POST",
        url: "some.php",
        data: "name=John&location=Boston",
        success: function(msg){
            alert( "Data Saved: " + msg );
        }
    });
}

function showStateJSON(json){
    console.log("[");
    for(var i=0, len = json.length; i < len ; ++i){
        var obj = {id:i, label:json[i]};
        var json_string = JSON.stringify(obj);
        if(i != (len -1)){
            json_string = json_string.concat(',')
        }
        console.log(json_string);

    }
    console.log("]");
}

function showJSON(json){
     console.log("[");
    for(var i=0, len = json.length; i < len ; ++i){
         var json_string = JSON.stringify(json[i]);
        if(i != (len -1)){
            json_string = json_string.concat(',');
        }
        console.log(json_string);
    }
    console.log("]");
}

//GUI上の初期状態、目標状態からファイルを作る
function make_ObjFile(){
    //saveJSON();
    var initStateS = init_data;
    //var initStateS = document.getElementById("InitState").childNodes;//現在の初期状態リストを得る
    var pat_is =  /\sis\s/;//" is "と同じ
    var pat_space = /\s/g;//スペース
    var pat_prepos = /((\s)|^)((on)|(in))\s/;//前置詞
    var pat_color = /^((red)|(blue)|(green)|(crimson))$/i;//色パターン　大文字、小文字を区別しない
    var pat_shape = /^((sphere)|(square)|(triangle))$/i;//形パターン 大文字、小文字を区別しない
    var obj_list = new Array();//オブジェクトの情報(名前、色、形)リスト
    var env_list = new Array();//obj_list に入らないほかの情報リスト
    for(var i = 0, len = initStateS.length; i < len; ++i){
        //var splited1 = initStateS[i].value.split(pat_is);//isの左右で分ける
        var splited1 = initStateS[i].split(pat_is);//isの左右で分ける
        var obj_id = splited1[0];//オブジェクトの名前
        if(!splited1[1].match(pat_prepos)){//前置詞を含んでいなかった場合
            var splited2 = splited1[1].split(pat_space);//スペースで分けれるだけ分ける
            var obj_color = null;
            var obj_shape = null;
            for(var j = 0, len2 = splited2.length; j < len2 ; ++j){
                if(splited2[j].match(pat_color)){
                    obj_color = splited2[j];
                }
                else if(splited2[j].match(pat_shape)){
                    obj_shape = splited2[j];
                }
            }
            //console.log(splited2);

            var exist = false;
            for(var k = 0, len3 = obj_list.length; k < len3; ++k){
                if(obj_list[k].id == obj_id){//既存のid(オブジェクトであった場合)
                    if(obj_color != null){
                        obj_list[k].color = obj_color;
                    }
                    else if(obj_shape != null){
                        obj_list[k].shape = obj_shape;
                    }
                    exist = true;
                    break;
                }
            }

            if(!exist && (obj_color != null || obj_shape != null)){
                var new_obj = {id:obj_id,color:obj_color,shape:obj_shape};
                obj_list.push(new_obj);
            }

        }
        else{//前置詞がなかった場合
            //env_list.push(initStateS[i].value);
            env_list.push(initStateS[i]);
        }
        //console.log(splited1);
    }

   showJSON(obj_list);
   showStateJSON(env_list);
   showStateJSON(init_data);
   showStateJSON(target_data);
}



function init_Animation(){
  // 描画領域のサイズ決定
  //$('#Anim').css('top',mIdData.length*100);
  $('#Anim').css('top',init_anim_data.length*100);
  // 初期状態の作成
  PrepareInitialState();
}

// 指定されたリストのi番目の要素についてID、座標をセットする
function SetData2(aIdData,aStateData){
  console.log("aId="+aIdData+"aStateData"+aStateData);
  mId = aIdData;
  var tStates = aStateData;
  //console.log('i='+i+' ts='+tStates);

  // Groundのためー１
  // mYState = (-tStates[0]+init_anim_data.length-1)*100;
  mYState = (-tStates[0])*100;
  mXState = tStates[1]*100;
}


// 初期状態の作成
function PrepareInitialState(){
  // 描画ボタンをクリック可能に
  //$("#DrawingButton").prop("disabled", false);
  // var anim = document.getElementById('Anim');

  // Groundにへんこう
  var anim = document.getElementById('Ground');

  delete_children(anim);//ブロックが存在すれば全消去

  add_arm();
  // add_hole();
  for (var i = 0; i < init_anim_data.length; i++) {
    SetData2(init_anim_data[i].id,init_anim_data[i].coordinate);
    mShape = init_anim_data[i].shape;
    mColor = init_anim_data[i].color;
    add_box2();
  }
}


// プロセスの描画
function OperationProcess(){
  if(exec.step < todo_array.length){
    SetData2(process_array[exec.step].Target,process_array[exec.step].New);
    set_arm();

　　// アームを動かすブロックにセットするために遅延
    setTimeout(move_box,1500);
    // move_box();
  }
}


function set_arm(){
   var tSetArm = $('#box'+mId).position();

// アームをセットするまでの時間は早めに設定
  $('#arm1')
 .animate({'top':mAreaTop-25},300)
 .animate({'left':tSetArm.left},300)
 .animate({'top':tSetArm.top-25},300)
}


// 描画状態のリセット
function ResetState() {
    // ブロックを初期状態に移動させる
    for (var i = 0; i < init_anim_data.length; ++i) {
        SetData2(init_anim_data[i].id,init_anim_data[i].label);
        move_box2();
    }
    $('#arm1')
   .animate({'top':mAreaTop-25,'left':0},1500)

}

// アームの作成
function add_arm()
{
  var div_element = document.createElement("div");
   div_element.setAttribute('class','arm');
   div_element.setAttribute('id','arm1');
  // var parent_object = document.getElementById("Anim");

  // Groundにへんこう
  var parent_object = document.getElementById("Ground");

  parent_object.appendChild(div_element);
  $('#arm1').css({'position':'absolute','left':0,'top':mAreaTop-25});
}


// // 穴の作成
// function add_hole()
// {
//   var div_element = document.createElement("div");
//    div_element.setAttribute('class','hole');
//    div_element.setAttribute('id','hole1');
//   // var parent_object = document.getElementById("Anim");
//
//   // Groundにへんこう
//   var parent_object = document.getElementById("Ground");
//
//   parent_object.appendChild(div_element);
//   $('#hole1').css({'position':'absolute','left':100,'top':400});
// }



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
    //*
    // make_TaskList('Doing',doing_array);
    // make_TaskList('Done',done_array);
    setTimeout(function(){anim_end_ary[exec.step] = true; },3000);
    $('#arm1')
   .animate({'top':mAreaTop-25},500)
   .animate({'left':mXState},500)
   .animate({'top':mYState-25},500)
     $('#box'+mId)
    .animate({'top':mAreaTop},500)
    .animate({'left':mXState},500)
    .animate({'top':mYState},500)

    //*/
    // console.log('step='+exec.step+' tgt='+exec.target.toString());
}

//ボックスの移動　やり直し用
function move_box2(){
     $('#box'+mId)//id を指定する
    .animate({'top':mAreaTop},500)
    .animate({'left':mXState},500)
    .animate({'top':mYState},500)
}

//ボックスの移動　穴に落とす
function move_box3(){
  $('#arm1')
 .animate({'left':mXState},500)
 // .animate({'top':mYState},500)
     $('#box'+mId)//id を指定する
    .animate({'left':mXState},500)
    .animate({'top':mYState},500)
}


function _dragstart(event){
  event.dataTransfer.setData("text", event.target.id);
}

function _dragover(event){
	event.preventDefault();
}

function _drop(event){
	var id_name = event.dataTransfer.getData("text");
	var drag_elm =document.getElementById(id_name);
	// event.currentTarget.appendChild(drag_elm);
  // docuent.Ground.appendChild(drag_elm);

  var parent_object = document.getElementById("Ground");
  parent_object.appendChild(drag_elm);

  // alert($('#boxa').parents('div').attr('id'));
	event.preventDefault();
}

function SetBox(event){
  var id_name = event.dataTransfer.getData("text");
  var tPanelId = event.currentTarget.id;
  var tPanelPosition = $('#'+tPanelId).position();

  mXState = tPanelPosition.left;
  mYState = tPanelPosition.top;

  var tIdScript = id_name.replace( /box/g , "" ) ;

  var tNum = mNameList.indexOf(tIdScript);
  init_anim_data[tNum].coodinate[0] = mYState/100;
  init_anim_data[tNum].coodinate[1] = mXState/100;
  $('#'+id_name).css({'left':mXState,'top':mYState});
  // alert(init_anim_data[tNum].coodinate[1]);
  AboveCheck(tPanelId,tNum);
}

function AboveCheck(aPanelId,aNum){
  var tPanelId = aPanelId;
  var tPanelPosition = $('#'+tPanelId).position();
  var tNum = aNum;
  var tNewYState = mAreaTop-200;
  if(init_anim_data[tNum].shape != "triangle"){
    if(init_anim_data[tNum].coodinate[0]*100 > mAreaTop){
      tNewYState = tPanelPosition.top-100;
      // alert(init_anim_data[tNum].coodinate[0]);
    }
    // if(init_anim_data[tNum].coodinate[1] != tPanelPosition.left){
    //   tNewYState = $('#'+tPanelId).position().top-100;
    // }
  }

  $('#'+tPanelId).css({'top':tNewYState});
}
