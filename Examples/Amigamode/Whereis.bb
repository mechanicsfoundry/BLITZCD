
ff$=""
doneflag.w=0

;--------------------------------------------------------------

NEWTYPE.FileInfoBlock
   fib_DiskKey.l
   fib_DirEntryType.l
   fib_FileName.b[108]
   fib_Protection.l
   fib_EntryType.l
   fib_Size.l
   fib_NumBlocks.l
   ds_Days.l
   ds_Minute.l
   ds_Tick.l
   fib_Comment.b[80]
   fib_Reserved.b[36]
End NEWTYPE

Dim months$(12)
For l=1 To 12
    Read months$(l)
Next
Data$ "Jan","Feb","Mar","Apr","May","Jun","Jul"
Data$ "Aug","Sep","Oct","Nov","Dec"

Dim month(13)
month(1)=0
month(2)=31
month(3)=31+29
month(4)=31+29+31
month(5)=31+29+31+30
month(6)=31+29+31+30+31
month(7)=31+29+31+30+31+30
month(8)=31+29+31+30+31+30+31
month(9)=31+29+31+30+31+30+31+31
month(10)=31+29+31+30+31+30+31+31+30
month(11)=31+29+31+30+31+30+31+31+30+31
month(12)=31+29+31+30+31+30+31+31+30+31+30
month(13)=1000

;--------------------------------------------------------------
#daysinquadyr=365*4+1                ;quadyr stands for 4 year cycle
#days1978=#daysinquadyr*494+366+365  ;days from 0/0/0 to 1/1/78

Function$ date{j.l}
  SHARED month(),months$()
  j+#days1978                   ;back to 0/0/0
  quadyr.w=Int(j/#daysinquadyr) ;how many quads since 0/0/0
  j-quadyr*#daysinquadyr        ;take off that many days
  yr.w=quadyr*4
  If j>366                     ;not a leap year
    j-366                      ;so remove leapyr days
    yr+1+Int(j/365)            ;got year
    j MOD 365                  ;number of days into year
    If j>(31+27) Then j+1      ;if after feb add dummy
  EndIf
  mth=1:While j+1>month(mth+1):mth+1:Wend ;find month
  day=j-month(mth)
  date$="0"+Str$(day+1):date$=Right$(date$,2)
  date$=date$+"-"+months$(mth)+"-"+Str$(yr)
  Function Return date$
End Function


;--------------------------------------------------------------
Function.w GetFiles{path$,ff$}
SHARED month$()

    nf.w=0

    fib.l=AllocMem_(280,65537)
    lock.l=Lock_(&path$,-2)
    c.l=Examine_(lock,fib)      ;entry=path$

    If c.l=0 Then Goto qqq

    While ExNext_(lock,fib)<>0

        If Joyb(0)<>0 Then Pop While:Goto qqq

        n$=Peek$(fib+8):n$=LCase$(n$)
        t=Peek.l(fib+4)

        If t<0          ;file
            found=0

            If Right$(ff$,1)="*"
                fs$=Left$(ff$,Len(ff$)-1)
                If Instr(n$,fs$,1)
                    found=1
                EndIf
            Else
                If ff$=n$ Then found=1
            EndIf

            If found=1
                *fib2.FileInfoBlock=fib
                by.l=*fib2\fib_Size
                days.l=*fib2\ds_Days
                minutes.l=*fib2\ds_Minute
                ticks.l=*fib2\ds_Tick
                secs.l=Int(ticks/50)
                hour.w=Int(minutes/60)
                minutes=minutes-(hour*60)
                date$=date{days}
                b$=Str$(by)
                p$=path$
                If Right$(p$,1)=":" OR Right$(p$,1)="/"
                    p$=p$+n$
                Else
                    p$=p$+"/"+n$
                EndIf
                p$=p$+String$(" ",48):p$=Left$(p$,48-Len(b$))
                NPrint p$," ",by," ",date$," ",hour,":",minutes,":",secs
            EndIf
        EndIf

        If t=>0          ;dir
            p$=path$
            If Right$(p$,1)=":" OR Right$(p$,1)="/"
                p$=p$+n$
            Else
                p$=p$+"/"+n$
            EndIf
            cc.l=GetFiles{p$,ff$}
        EndIf

        If Joyb(0)<>0 Then Pop While:Goto qqq

    Wend
qqq:
    UnLock_ lock
    FreeMem_ fib,280

    Function Return nf-1

End Function
;--------------------------------------------------------------


DefaultOutput
DefaultInput

If NumPars<1 OR NumPars>2
wz: NPrint "USAGE: Whereis FILE [DEVICE]"
    NPrint "e.g. WhereIs myfile dh0: (Device defaults to SYS:)"
    NPrint "FILE may end in a * to indicate the string is a search key."
    NPrint "e.g. WhereIs d* c: will find all files in c: with a `d' in them."
    NPrint "DEVICE must be a valid path name ending in a `:' or `/'."
    NPrint "Use left mouse button to quit.(Sorry)."
    End
EndIf

If Par$(1)="?"
    NPrint "WhereIs V1.6. Written By Paul Andrews In Blitz Basic 2."
    Goto wz
EndIf

ff$=Par$(1):ff$=LCase$(ff$)

If NumPars=2
    dd$=Par$(2)
Else
    dd$="sys:"
EndIf

lock.l=Lock_(&dd$,-2)
If lock<>0
    UnLock_ lock
Else
    NPrint "Device ",dd$," not found!!"
    End
EndIf

nn.l=GetFiles{dd$,ff$}

End


