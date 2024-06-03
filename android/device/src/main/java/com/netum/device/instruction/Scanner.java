package com.netum.device.instruction;

public class Scanner {

    /**
     * 获取软件版本号
     */
    public static final String ReadFirmwareVersion="$SW#VER";

    /**
     * 恢复出厂设置
     */
    public static final String RestoreToFactorySet = "%#IFSNO$B";

    /**
     * 读取传输模式设置
     */
    public static final String ReadInterfaceSetting = "%#IFSNO$";

    //region 工作方式
    /**
     * 普通工作模式
     */
    public static final String WorkMode_Normal  = "%#NORMD";
    /**
     * 存储工作模式
     */
    public static final String WorkMode_Store  = "%#INVMD";
    /**
     * 存储数据上传
     */
    public static final String StoreDataUpload  = "%#TXMEM";
    /**
     * 存储数据上传后清除
     */
    public static final String StoreDataUploadClear  = "%#TXMEM#C";
    /**
     * 当前存储总条数
     */
    public static final String StoreDataNumber = "%#+TCNT";
    /**
     * 存储总条数及空间占用情况
     */
    public static final String StoreDataNumberAndSpaceOccupancy  = "%#+TCNT#";
    /**
     * 清除存储条码
     */
    public static final String StoreDataClear = "%#*NEW*";
    /**
     * 自动存储模式关闭
     */
    public static final String StoreAutoSaveOff = "%AutoSav#Off";
    /**
     * 自动存储模式开启
     */
    public static final String StoreAutoSaveOn = "%AutoSav#On";
    //endregion

    //region 电源管理
    /**
     * 获取当前休眠时间
     */
    public static final String ReadSleeptime  = "$RF#ST";
    /**
     * 立即关机
     */
    public static final String PowerOff  = "$POWER#OFF";
    /**
     * 1分钟后休眠
     */
    public static final String SleepTime1Min  = "$RF#ST02";
    /**
     * 3分钟后休眠
     */
    public static final String SleepTime3Min  = "$RF#ST06";
    /**
     * 5分钟后休眠
     */
    public static final String SleepTime5Min  = "$RF#ST10";
    /**
     * 10分钟后休眠
     */
    public static final String SleepTime10Min  = "$RF#ST20";
    /**
     * 30分钟后休眠
     */
    public static final String SleepTime30Min  = "$RF#ST60";
    /**
     * 1小时后休眠
     */
    public static final String SleepTime1Hour  = "$RF#ST<0";
    /**
     * 2小时后休眠
     */
    public static final String SleepTime2Hour  = "$RF#STH0";
    /**
     * 从不休眠
     */
    public static final String NeverSleep  = "$RF#ST00";
    /**
     * 获取扫描枪电池电量
     */
    public static final String GetVolume  = "%BAT_VOL#";
    //endregion

    //region 消息提示
    /**
     * 蜂鸣器控制-静音
     */
    public static final String BeepMuteVolume = "$BUZZ#0";
    /**
     * 蜂鸣器控制-高音量
     */
    public static final String BeepHighVolume = "$BUZZ#1";
    /**
     * 蜂鸣器控制-中音量
     */
    public static final String BeepMiddleVolume = "$BUZZ#2";
    /**
     * 蜂鸣器控制-底音量
     */
    public static final String BeepLowVolume = "$BUZZ#3";
    /**
     * 蜂鸣器控制-高音调
     */
    public static final String BeepHighTone = "$BUZZ#4";
    /**
     * 蜂鸣器控制-底音调
     */
    public static final String BeepLowTone = "$BUZZ#5";
    /**
     * 振动马达关闭
     */
    public static final String VibrationDisable = "$MOTO#0";
    /**
     * 振动马达开启
     */
    public static final String VibrationEnable = "$MOTO#1";
    /**
     * SDK声音应答关闭
     */
    public static final String SDKAckBeepOff = "%ACKBEEP#0";
    /**
     * SDK声音应答开启
     */
    public static final String SDKAckBeepOn = "%ACKBEEP#1";
    /**
     * 基座连接蜂鸣音提示 开/关
     */
    public static final String BaseConnectBeep = "%ACKBEEP#2";
    /**
     * 自定义控制蜂鸣器命令
     */
    public static final String BeepCustomOpt = "$BUZZ#B";
    /**
     * 自定义控制蜂鸣器输出时间命令
     */
    public static final String BeepCustomTime = "$BUZZ#BK{0}{1}{2}";
    //endregion

    //region RTC时钟
    /**
     * 禁用时间戳
     */
    public static final String DisableTimeStamp="%RTCSTAMP#0";
    /**
     * 启用时间戳
     */
    public static final String EnableTimeStamp="%RTCSTAMP#1";
    /**
     * 时间戳设定日期时间,23/05/04,09:51:00
     */
    public static final String TimeStampSetFormat1="%RTCTIME#{0}";
    /**
     * 时间戳设定10位时间戳,1636530504
     */
    public static final String TimeStampSetFormat2="%RTCSTAMP#{0}";
    //endregion

    //region 模组设置
    /**
     * 获取CCD/二维模组型号
     */
    public static final String GetCCDModuleType = "%MODULESN#";
    /**
     * 设置CCD/二维模组型号
     */
    public static final String SetCCDModuleType = "%MODULESN#{0}#";
    //endregion

    //region 扫描模式
    /**
     * 按键保持扫描模式
     */
    public static final String KeyScanMode = "%SCMD#00#";
    /**
     * 连续扫描模式
     */
    public static final String ContinueScanMode = "%SCMD#01#";
    /**
     * 按键脉冲扫描模式
     */
    public static final String KeyPulseScanMode = "%SCMD#02#";
    /**
     * 主机触发模式
     */
    public static final String HostTriggerMode = "%SCMD#03#";
    /**
     * 解码超时时间3秒
     */
    public static final String DecodeOvertime3S = "%SCMD#3000D";
    /**
     * 解码超时时间6秒
     */
    public static final String DecodeOvertime6S = "%SCMD#6000D";
    /**
     * 解码间隔时间0.5秒
     */
    public static final String IntervalTime05S = "%SCMD#0500I";
    /**
     * 解码间隔时间0.5秒
     */
    public static final String IntervalTime10S = "%SCMD#1000I";
    /**
     * 立即扫描（n=1~7）S
     */
    public static final String SoftTrigger = "%SCANTM#{0}#";
    //endregion

    //region 键盘设置
    /**
     * 读取当前选择的国家语言
     */
    public static final String ReadCurrentSelectedLanguage = "$LAN#";
    /**
     * 美国英语键盘
     */
    public static final String KeyboardSelectedLanguage_EN = "$LAN#EN";
    /**
     * 法国键盘
     */
    public static final String KeyboardSelectedLanguage_FR = "$LAN#FR";
    /**
     * 德国键盘
     */
    public static final String KeyboardSelectedLanguage_GE = "$LAN#GE";
    /**
     * 土耳其Q键盘
     */
    public static final String KeyboardSelectedLanguage_TK = "$LAN#TK";
    /**
     * 土耳其F键盘
     */
    public static final String KeyboardSelectedLanguage_TF = "$LAN#TF";
    /**
     * 葡萄牙键盘
     */
    public static final String KeyboardSelectedLanguage_PT = "$LAN#PT";
    /**
     * 西班牙键盘
     */
    public static final String KeyboardSelectedLanguage_ES = "$LAN#ES";
    /**
     * 捷克键盘
     */
    public static final String KeyboardSelectedLanguage_CS = "$LAN#CS";
    /**
     * 意大利键盘
     */
    public static final String KeyboardSelectedLanguage_IT = "$LAN#IT";
    /**
     * 比利时法语键盘
     */
    public static final String KeyboardSelectedLanguage_FB = "$LAN#FB";
    /**
     * 巴西-葡萄牙语键盘
     */
    public static final String KeyboardSelectedLanguage_PB = "$LAN#PB";
    /**
     * 加拿大法语键盘（传统）
     */
    public static final String KeyboardSelectedLanguage_FC = "$LAN#FC";
    /**
     * 克罗地亚键盘
     */
    public static final String KeyboardSelectedLanguage_HR = "$LAN#HR";
    /**
     * 斯洛伐克键盘
     */
    public static final String KeyboardSelectedLanguage_SK = "$LAN#SK";
    /**
     * 丹麦键盘
     */
    public static final String KeyboardSelectedLanguage_DA = "$LAN#DA";
    /**
     * 芬兰键盘
     */
    public static final String KeyboardSelectedLanguage_FI = "$LAN#FI";
    /**
     * 匈牙利键盘
     */
    public static final String KeyboardSelectedLanguage_HU = "$LAN#HU";
    /**
     * 拉丁美洲(西班牙语)键盘
     */
    public static final String KeyboardSelectedLanguage_EL = "$LAN#EL";
    /**
     * 荷兰键盘
     */
    public static final String KeyboardSelectedLanguage_NL = "$LAN#NL";
    /**
     * 挪威键盘
     */
    public static final String KeyboardSelectedLanguage_NO = "$LAN#NO";
    /**
     * 波兰键盘
     */
    public static final String KeyboardSelectedLanguage_PL = "$LAN#PL";
    /**
     * 塞尔维亚(拉丁文)键盘
     */
    public static final String KeyboardSelectedLanguage_SR = "$LAN#SR";
    /**
     * 斯洛文尼亚键盘
     */
    public static final String KeyboardSelectedLanguage_SL = "$LAN#SL";
    /**
     * 瑞典键盘
     */
    public static final String KeyboardSelectedLanguage_SV = "$LAN#SV";
    /**
     * 瑞士-德语键盘
     */
    public static final String KeyboardSelectedLanguage_DS = "$LAN#DS";
    /**
     * 英国英语键盘
     */
    public static final String KeyboardSelectedLanguage_UK = "$LAN#UK";
    /**
     * 日语键盘
     */
    public static final String KeyboardSelectedLanguage_JP = "$LAN#JP";
    /**
     * 泰语键盘
     */
    public static final String KeyboardSelectedLanguage_TH = "$LAN#TH";
    /**
     * ALT通用键盘
     */
    public static final String KeyboardSelectedLanguage_AG = "$LAN#AG";
    /**
     * ALT单字节特殊字符键盘
     */
    public static final String KeyboardSelectedLanguage_RU = "$LAN#RU";
    //endregion

    //region 字符设置
    /**
     * 清除格式
     */
    public static final String PrefixSuffixHideClearFormat = "$DATA#0";
    /**
     * 允许后缀输出
     */
    public static final String AllowSuffixOutput = "$DATA#1";
    /**
     * 允许前缀输出
     */
    public static final String AllowPrefixOutput = "$DATA#2";
    /**
     * 允许隐藏条码尾部内容
     */
    public static final String AllowHidBarcodeSuffix = "$DATA#3";
    /**
     * 允许隐藏条码中部内容
     */
    public static final String AllowHidBarcodeContent = "$DATA#4";
    /**
     * 允许隐藏条码首部内容
     */
    public static final String AllowHidBarcodePrefix = "$DATA#5";
    //endregion

    enum ModuleType {
        Module_212X,
        Module_C06C,
        Module_4710,
        Module_280H,
        Module_4680,
        Module_EX25,
        Module_RFID
    }
}
