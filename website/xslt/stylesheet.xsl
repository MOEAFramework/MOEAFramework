<?xml version="1.0"?>
<!DOCTYPE some_name [ 
<!ENTITY nbsp "&#160;">
<!ENTITY copy "&#169;">
<!ENTITY epsilon "&#949;">
]>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="html" indent="yes" />

	<xsl:param name="filename"></xsl:param>
	<xsl:param name="version"></xsl:param>
	<xsl:param name="today"></xsl:param>
	<xsl:param name="tracker"></xsl:param>
	
	<xsl:template match="comment()">
    	<xsl:copy-of select="." />
 	</xsl:template>

	<xsl:template match="/page">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html></xsl:text>

		<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
			<head>
				<meta name="author" content="David Hadka" />
				<meta name="description">
					<xsl:attribute name="content"><xsl:value-of select="description" /></xsl:attribute>
				</meta>
				<meta name="keywords" content="multiobjective, multicriteria, moea, moeas, nsga, nsgaii, moead, gde3, optimization, optimisation, evolutionary, genetic, algorithm, programming, differential, grammatical, evolution, particle, swarm, framework, library, java, lgpl, open, source, oss, download, free" />
				<meta name="robots" content="index, follow, noarchive" />
				<meta name="googlebot" content="noarchive" />
				<meta http-equiv="pragma" content="no-cache" />

				<title><xsl:value-of select="title" /></title>
				<link rel="shortcut icon" href="/favicon.ico" />

				<!-- include the Styleshout Coolblue stylesheet -->
				<link rel="stylesheet" type="text/css" media="screen" href="css/screen.css" />
				
				<xsl:copy-of select="header/node()" />
			</head>
			<body itemscope="itemscope" itemtype="http://schema.org/SoftwareApplication">
				<!-- header -->
				<div id="header-wrap">
					<div id="header">
						<a name="top"></a>
						<img id="logo" src="images/logo2_small.png" alt="MOEA Framework logo" width="103" height="100" itemprop="image" />
						<h1 id="logo-text">
							<a href="index.html" title="" itemprop="name">MOEA Framework</a>
						</h1>
						<p id="slogan">A Free and Open Source Java Framework for Multiobjective Optimization</p>

						<div id="nav">
							<ul>
								<li>
									<xsl:if test="$filename = 'index.xml'">
										<xsl:attribute name="id">current</xsl:attribute>
									</xsl:if>
									<a href="index.html">Home</a>
								</li>
								<li>
									<xsl:if test="$filename = 'examples.xml'">
										<xsl:attribute name="id">current</xsl:attribute>
									</xsl:if>
									<a href="examples.html">Examples</a>
								</li>
								<li>
									<xsl:if test="$filename = 'downloads.xml'">
										<xsl:attribute name="id">current</xsl:attribute>
									</xsl:if>
									<a href="downloads.html">Downloads</a>
								</li>
								<li>
									<xsl:if test="$filename = 'documentation.xml'">
										<xsl:attribute name="id">current</xsl:attribute>
									</xsl:if>
									<a href="documentation.html">Documentation</a>
								</li>
								<li>
									<xsl:if test="$filename = 'support.xml'">
										<xsl:attribute name="id">current</xsl:attribute>
									</xsl:if>
									<a href="support.html">Support</a>
								</li>
								<li>
									<xsl:if test="$filename = 'donate.xml'">
										<xsl:attribute name="id">current</xsl:attribute>
									</xsl:if>
									<a href="donate.html">Donate</a>
								</li>
							</ul>
						</div>
					</div>
				</div>

				<!-- body -->
				<div id="content-wrap" class="clear">
					<div id="content">
						<div id="main">
							<xsl:copy-of select="content/node()" />
						</div>

						<!-- sidebar -->
						<div id="sidebar">
							<div class="sidemenu downloads">
								<h3>Downloads</h3>
								<p>
									Current Version: <span itemprop="softwareVersion"><b><xsl:value-of select="$version" /></b></span><br />
									Released: <span itemprop="dateModified"><xsl:value-of select="$today" /></span>
									<meta itemprop="operatingSystem" content="Windows" />
									<meta itemprop="operatingSystem" content="Linux" />
									<meta itemprop="operatingSystem" content="Mac" />
								</p>
								<ul>
									<li>
										<a href="https://sourceforge.net/projects/moeaframework/files/MOEAFramework-%VERSION%/MOEAFramework-%VERSION%-Demo.jar/download"
										    onClick="_gaq.push(['_trackEvent', 'Downloads', 'Demo', '%VERSION%']);"
										    itemprop="downloadUrl">
											Demo Application
										</a>
									</li>
									<li>
										<a href="https://sourceforge.net/projects/moeaframework/files/MOEAFramework-%VERSION%/MOEAFramework-%VERSION%.tar.gz/download"
										    onClick="_gaq.push(['_trackEvent', 'Downloads', 'Binary', '%VERSION%']);"
										    itemprop="downloadUrl">
											Compiled Binaries
										</a>
									</li>
									<li>
										<a href="https://sourceforge.net/projects/moeaframework/files/MOEAFramework-%VERSION%/MOEAFramework-%VERSION%-Source.tar.gz/download"
                                            onClick="_gaq.push(['_trackEvent', 'Downloads', 'Source', '%VERSION%']);">
											Source Code
										</a>
									</li>
									<li>
										<a href="https://sourceforge.net/projects/moeaframework/files/MOEAFramework-%VERSION%/MOEAFramework-%VERSION%-Manual.pdf/download"
											onClick="_gaq.push(['_trackEvent', 'Downloads', 'Manual', '%VERSION%']);">
											User Manual	
										</a>
									</li>
								</ul>
								<p>
									Using Maven?  Add our <a href="downloads.html#maven">dependency</a>.<br />
									Looking for a <a href="downloads.html#previous">previous release</a>?
								</p>
							</div>

							<div class="sidemenu">
								<h3>License</h3>
								<p>
									Licensed under the <a href="http://www.gnu.org/licenses/lgpl.html">GNU Lesser General Public License</a>.
								</p>
							</div>
							
							<!--
							<div class="sidemenu">
								<h3>Paperback Manual</h3>
								<p>
									<a href="http://www.lulu.com/product/21284804" class="tooltip" title="All proceeds from book sales support the continued development and maintenance of the MOEA Framework.">
										Get your paperback copy of the user manual today and support us!<br />
										<img align="middle" src="images/product_thumbnail.jpg" width="100px" style="padding: 0;"></img><br />
									</a>
									<span style="font-size: 12pt; font-weight: bold;"><b>Price: <font color="red">$20.99</font></b></span><br />
									<span style="font-size: 10pt;">You Save: <font color="red">$9.00 (30%)</font></span><br />
								</p>
							</div>
							-->
							
							<xsl:choose>
								<xsl:when test="$filename = 'index.xml' or $filename = 'documentation.xml' or $filename = 'support.xml' or $filename = 'contribute.xml' or $filename = 'credits.xml' or $filename = 'donate.xml'">
									<div class="sidemenu">
										<h3></h3>
										<p>
											<script type="text/javascript">
												google_ad_client = "ca-pub-5610668616453880";
												google_ad_slot = "9118867796";
												google_ad_width = 200;
												google_ad_height = 200;
											</script>
											<script type="text/javascript"
													src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
											</script>
										</p>
									</div>
								</xsl:when>
								<xsl:otherwise>
									<div class="sidemenu">
										<h3></h3>
										<p>
											<script type="text/javascript">
												google_ad_client = "ca-pub-5610668616453880";
												google_ad_slot = "7891799492";
												google_ad_width = 160;
												google_ad_height = 600;
											</script>
											<script type="text/javascript"
													src="http://pagead2.googlesyndication.com/pagead/show_ads.js">
											</script>
										</p>
									</div>
								</xsl:otherwise>
							</xsl:choose>
						</div>
					</div>
				</div>

				<!-- footer -->
				<div id="footer-bottom">
					<div class="bottom-left">
						&copy; 2009-2015 <strong>Dave Hadka and others</strong>
    					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						Website template by <a href="http://www.styleshout.com/">styleshout</a>
    					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<xsl:comment> %TRACKER% </xsl:comment>
					</div>

					<div class="bottom-right">
						<a href="index.html">Home</a> |
						<a href="credits.html">Credits</a> |
						<b><a href="#top">Back to Top</a></b>
					</div>
				</div>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>
