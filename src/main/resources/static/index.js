var server = {
    ws:null,
    callbacks:{},   //消息回调函数
    init(){
        this.ws = new WebSocket("ws://127.0.0.1:11551?uname=test&pwd=123456");
        var _ws = this.ws;
        var _this = this;
        _ws.onopen = function(){
            console.log("ws 连接成功!")
            //保持连接
            setInterval(function(){
                _ws.send('ping')
            },1000 * 60)
        }
    
        _ws.onmessage = function(msg){
            msg = msg.data
            console.log('<==')
            console.log(msg)
            if('pong' == msg || '1' == msg){
                return
            }

            var msgObj = JSON.parse(msg)
            var callback = _this.callbacks[msgObj.id]
            if(callback){
                callback(msgObj.data)
            }else{
                console.log("未处理消息 msg = " + msg)
            }
        }

        _ws.onerror = function(e){
            console.warn("连接出错")
            console.warn(e)
            _ws.close()
        }

        _ws.onclose = function(){
            console.warn("连接断开")
            //setTimeout(function(){
            //    _this.init()
            //},1000)
        }
    },
    get(callback){
        var id = "id_" + Math.random()
        this.callbacks[id] = callback

        this.send({
            action:'get',
            id: id,
        })
    },
    send(msgObj){
        var msg = JSON.stringify(msgObj);
        this.ws.send(msg)
        console.log("==>")
        console.log(msg)
    },
    set(opt,callback){
        var id = "id_" + Math.random()
        this.callbacks[id] = callback

        this.send({
            action:'set',
            id: id,
            data:opt,
        })
    }
}

server.init()


/** 加载表格 */
function load() {
    server.get(function(options){
        luckysheet.destroy()
        luckysheet.create(JSON.parse(options))
        console.log("init...")
    })
}

/** 保存表格 */
function save() {
    var options = {
        title: 'TD游戏配置',
        lang: 'zh',
        container: 'luckysheet',
        showinfobar: false,
        data: [],
    }

    var luckysheetfile = luckysheet.getLuckysheetfile()
    if(!luckysheetfile) return;

    for (const index in luckysheetfile) {
        var sheet = luckysheetfile[index]
        options.data[index] = {
            name: sheet.name,
            index: index,
            column: sheet.data[0].length,
            row: sheet.data.length,
        }

        options.data[index].celldata = []
        var i = 0;
        for (const r in sheet.data) {
            for (const c in sheet.data[r]) {
                options.data[index].celldata[i++] = {
                    r: r,
                    c: c,
                    v: sheet.data[r][c]
                }
            }
        }
    }

    server.set(JSON.stringify(options),function(){
        console.log("同步成功")
    })
}