/*
	Copyright (c) 2004-2008, The Dojo Foundation All Rights Reserved.
	Available via Academic Free License >= 2.1 OR the modified BSD license.
	see: http://dojotoolkit.org/license for details
*/


if(!dojo._hasResource["dojox.charting.plot2d.Areas"]){
dojo._hasResource["dojox.charting.plot2d.Areas"]=true;
dojo.provide("dojox.charting.plot2d.Areas");
dojo.require("dojox.charting.plot2d.Default");
dojo.declare("dojox.charting.plot2d.Areas",dojox.charting.plot2d.Default,{constructor:function(){
this.opt.lines=true;
this.opt.areas=true;
}});
}
