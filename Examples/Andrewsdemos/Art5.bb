#size=15  ;CHANGE ME!!! (lower for less-grunty computers)
#balls=6

;yet another 3D landscape demo


#bsize=(#size+1)*(#size+1)

NEWTYPE.corner
  x.q:y:z:sx.w:sy:fx:fy
End NEWTYPE

Dim hth.corner(#size,#size)
; a brutal demo of using pointers on arrays - ONLY FOR THE HARDCORE!

BLITZ

Function.q hite{x,y}
  x*Pi:y*Pi
  first=y*Sin(y+x*Cos(x))
  second=x*Cos(x+y*Sin(y))
  t=Sqr(first*first+second*second)/10
  Function Return t
End Function

BitMap 0,640,256,1:BitMap 1,640,256,1:Mouse On
Slice 0,44,640,256,$fff9,1,0,32,640,640:Show 0:RGB 1,15,13,13

For cnta=0 To #size
  For cntb=0 To #size
    *p.corner=hth(cnta,cntb)
    tx=(cnta/#size)*4-2
    ty=(cntb/#size)*4-2
    *p\x=(tx+ty) ASR 1
    *p\y=(tx-ty)ASR 1

    *p\z=hite{*p\x,*p\y}
  Next
Next

NEWTYPE .hpt
  x.q:y:z:vx:vy
End NEWTYPE

Dim hpt.hpt(50)

For cnt.w=0 To #balls
  hpt(cnt)\x=Rnd*#size,Rnd*#size,(Rnd-0.5)*2
  hpt(cnt)\vx=(Rnd+0.2) ASR 1,(Rnd+0.2) ASR 1
Next


Repeat
  VWait:Show db:db=1-db:Use BitMap db:Cls
;  ang=MouseX/320*Pi     ; try me and moce the mouse
  ang +0.05:If ang>(2*Pi) Then ang-(2*Pi)

  Gosub MoveIt
  qc=Cos(ang):qs=Sin(ang)
  Gosub Draw
Until Joyb(0)<>0
End


.MoveIt
  *h.hpt=hpt(0)
  For tt=0 To 5;10
    x=*h\x+*h\vx
    If x<0 OR x>#size
      *h\vx=-*h\vx
    Else
      *h\x=x
    EndIf

    y=*h\y+*h\vy
    If x<0 OR x>#size
      *h\vy=-*h\vy
    Else
      *h\y=y
    EndIf


    *h+SizeOf.hpt
  Next

Return

.Draw
  For cnta=0 To #size
    For cntb=0 To #size
      *p.corner=hth(cnta,cntb)
      tx=*p\x;*qc-*p\y*qs
      ty=*p\y;x*qs+*p\y*qc
      tz=*p\z

      tot=0:tz=0
      For tt=0 To 5;10
        *h.hpt=hpt(tt)
        dx=cnta-*h\x
        dy=cntb-*h\y
        dist=dx*dx+dy*dy

        fac=1/(dist+0.1)
        tot=tot+fac
        tz=tz+fac**h\z
      Next
      tz=tz/tot

      rx=tx
      ry=ty*0.866 -0.5*tz
      rz=ty*0.5+0.866*tz

      fac=1/(ry+4)
      *p\sx=320+(rx)*fac ASL 10,95-(rz)*fac ASL 9   ;setup screen co-ords..
    Next
  Next

  For cnta=0 To #size-1
    For cntb=0 To #size-1
      *p.corner=hth(cnta,cntb)
      *p1.corner=*p +SizeOf.corner             ; the next point across
      *p2.corner=*p1+SizeOf.corner*(#size)   ; the next point down
      Line *p\sx,*p\sy,*p1\sx,*p1\sy,1
      Line *p\sx,*p\sy,*p2\sx,*p2\sy,1   ; draw all the L's..
    Next
  Next

Return
