<#import "ui.ftl" as ui>
<@ui.header title=currentUser.alias />
	<@ui.sidebar />
	<div class="span10">
		<ul class="breadcrumb" style="margin-bottom: 5px;">
			<li><a href="/blog/gaoyoubo">首页</a> <span class="divider">/</span></li>
			<li><a href="/admin/home">管理</a> <span class="divider">/</span></li>                  
			<li class="active">我关注的动态</li>
	    </ul>
	    <div class="back-right-inner">
	        <ul class="nav nav-tabs">
	          <li class="active">
	            <a href="/admin/home">我关注的动态&nbsp;<span class="badge badge-info">0</span></a>
	          </li>
	          <li><a href="/admin/home/my">我的动态&nbsp;</a></li>
	          <li><a href="/admin/home/all">全站动态&nbsp;</a></li>
	        </ul>
	        
	        <ul class="nav nav-pills">
			  <li class="active">
			    <a href="/admin/home/follows">全部</a>
			  </li>
			  <li><a href="/admin/home/follows?optType=1">发表文章</a></li>
			  <li><a href="/admin/home/follows?optType=2">博客评论</a></li>
			  <li><a href="/admin/home/follows?optType=3">添加关注</a></li>
			  <li><a href="/admin/home/follows?optType=4">文章收藏</a></li>
			  <li><a href="/admin/home/follows?optType=5">评论回复</a></li>
			</ul>
	    </div>
    </div>
<#include "footer.ftl" />