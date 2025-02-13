;
; accessing exec level devices
;

NEWTYPE.Node:*ln_Succ.Node:*ln_Pred:ln_Type.b:ln_Pri:*ln_Name.b:End NEWTYPE

NEWTYPE.List:*lh_Head.Node:*lh_Tail:*lh_TailPred:lh_Type.b:l_pad:End NEWTYPE

NEWTYPE.MsgPort
  mp_Node.Node
  mp_Flags.b
  mp_SigBit.b
  *mp_SigTask.w
  mp_MsgList.List
End NEWTYPE

NEWTYPE.Message
  mn_Node.Node
  *mn_ReplyPort.MsgPort
  mn_Length.w
End NEWTYPE

NEWTYPE.IOStdReq
  io_Message.Message
  *io_Device.b           ;Device
  *io_Unit.b             ;Unit
  io_Command.w
  io_Flags.b
  io_Error.b
  io_Actual.l
  io_Length.l
  *io_Data.b
  io_Offset.l
;add particulars to device here
  rate.w:pitch:mode:sex:chmask.l:nmmask.w:vol:sampfreq
  mouths.b:chanmask.b:numchan.b:pad.b
End NEWTYPE

  #_INVALID=0:#_RESET=1:#_READ=2:#_WRITE=3:#_UPDATE=4
  #_CLEAR=5:#_STOP=6:#_START=7:#_FLUSH=8:#_NONSTD= 9

;
;initialise messageport and iorequest for talking to device
;

DEFTYPE .IOStdReq myio
DEFTYPE .MsgPort myport

myport\mp_Node\ln_Type=4
myport\mp_MsgList\lh_Head=&myport\mp_MsgList\lh_Tail
myport\mp_MsgList\lh_TailPred=&myport\mp_MsgList\lh_Head

myio\io_Message\mn_Node\ln_Type=5
myio\io_Message\mn_ReplyPort=&myport
myio\io_Message\mn_Length=SizeOf.IOStdReq

;
; attempt to open device
;

If OpenDevice_("narrator.device",0,myio,0)<>0 Then End

signal.l=AllocSignal_(-1):If signal<0 Then End
myport\mp_SigBit=signal
myport\mp_SigTask=FindTask_(0)

myio\chmask=$03050a0c,4,64

a$="SIH5GEREHT SMOW5KIHNX"+Chr$(0)  ;PHONEMES ONLY!
myio\io_Command=#_WRITE
myio\io_Data=&a$
myio\io_Length=Len(a$)+1
SendIO_ myio
NPrint "finished!"
MouseWait
CloseDevice_ myio
FreeSignal_ signal
MouseWait
End

