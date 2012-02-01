<html>
<head>
<link rel="stylesheet" href="bootstrap.min.css">
<link rel="stylesheet" href="core.css">
<script src="jquery.js"></script>
<script src="bootstrap-tabs.js"></script>
</head>
<title>${projectName} ${projectVersion} Release</title>
<body>

	<div class="container">
		<div class="page-header">
			<h1>
				${projectName} ${projectVersion} <small>
					${end.authorIdent.when?string("EEEEE, MMMMM d yyyy")} -
					${start.authorIdent.when?string("EEEEE, MMMMM d yyyy")}</small>
			</h1>
		</div>
		<div class="row">
			<div class="span12">
				<h3>Quick Facts</h3>
				<ul>
					<#assign commitsLabel = "${commits} commits">
					<li>${getCompare(commitsLabel)}</li>
					<li>${authors?size} authors</li>
					<li>${committers?size} committers</li>
					<li>${linesAdded} lines added</li>
					<li>${linesEdited} lines changed</li>
					<li>${linesDeleted} lines removed</li>
					<li>${added?size} files added</li>
					<li>${modified?size} files edited</li>
					<li>${deleted?size} files removed</li>
					<li>${renamed?size} files renamed/moved</li>
					<li>Started with commit <code>${getCommitName(end)}</code> by
						${end.authorIdent.name}
					</li>
					<li>Ended with commit <code>${getCommitName(start)}</code> by
						${start.authorIdent.name}
					</li>
				</ul>
			</div>
		</div>
		<div class="page-header" id="contributors">
			<h1>
				Contributors <small>who made the changes</small>
			</h1>
		</div>
		<div class="row">
			<div class="span5">
				<h3>
					Authors <small>${authors?size}</small>
				</h3>
				<ul>
					<#list authors as author>
					<li>${author}&nbsp;&nbsp;<span class="label success">${getAuthoredCommits(author)}</span></li>
					</#list>
				</ul>
			</div>
			<div class="span5">
				<h3>
					Committers <small>${committers?size}</small>
				</h3>
				<ul>
					<#list committers as committer>
					<li>${committer}&nbsp;&nbsp;<span class="label success">${getCommittedCommits(committer)}</span></li>
					</#list>
				</ul>
			</div>
			<div class="span5">
				<h3>
					First Timers <small>${firstTimers?size}</small>
				</h3>
				<ul>
					<#list firstTimers as committer>
					<li>${committer}&nbsp;&nbsp;<span class="label success">${getAuthoredCommits(committer)}</span></li>
					</#list>
				</ul>
			</div>
		</div>
		<div class="page-header" id="commits">
			<h1>
				Commits <small>biggest changes in this release</small>
			</h1>
		</div>
		<div class="row">
			<div class="span8">
				<h3>Most Lines Changed</h3>
				<ol>
					<#list mostLines as impact>
					<li><code>${getCommitShortName(impact.commit)}</code> by
						${parseCommit(impact.commit).authorIdent.name}&nbsp;&nbsp;<span
						class="label success">+${impact.add}</span> <span
						class="label warning">${impact.edit}</span> <span
						class="label important">-${impact.delete}</span></li> </#list>
				</ol>
			</div>
			<div class="span8">
				<h3>Most Files Changed</h3>
				<ol>
					<#list mostFiles as impact>
					<li><code>${getCommitShortName(impact.commit)}</code> by
						${parseCommit(impact.commit).authorIdent.name}&nbsp;&nbsp;<span
						class="label success">+${impact.add}</span> <span
						class="label warning">${impact.edit}</span> <span
						class="label important">-${impact.delete}</span></li></#list>
				</ol>
			</div>
		</div>
		<div class="page-header" id="files">
			<h1>
				Files <small>which were added, edited, and deleted</small>
			</h1>
		</div>
		<div class="row">
			<div class="span4">
				<h3>
					Edited <small>${modified?size}</small>
				</h3>
				<ul>
					<#list modified as edit>
					<li>${edit}</li> </#list>
				</ul>
			</div>
			<div class="span4">
				<h3>
					Added <small>${added?size}</small>
				</h3>
				<ul>
					<#list added as add>
					<li>${add}</li> </#list>
				</ul>
			</div>
			<div class="span4">
				<h3>
					Deleted <small>${deleted?size}</small>
				</h3>
				<ul>
					<#list deleted as delete>
					<li>${delete}</li> </#list>
				</ul>
			</div>
			<div class="span4">
				<h3>
					Renamed/Moved <small>${renamed?size}</small>
				</h3>
				<ul>
					<#list renamed as rename>
					<li>${rename}</li> </#list>
				</ul>
			</div>
		</div>
	</div>
</body>

</html>