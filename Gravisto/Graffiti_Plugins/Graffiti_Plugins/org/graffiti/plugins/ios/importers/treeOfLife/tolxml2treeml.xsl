<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- Match the root node -->
<xsl:template match="TREE">
<tree>
	<declarations>
		<attributeDecl name="name" type="String"/>
		<attributeDecl name="id" type="String"/>
		<attributeDecl name="extinct" type="Integer"/>
		<attributeDecl name="italicizename" type="Integer"/>
	</declarations>
	<xsl:apply-templates select="NODE"/>
</tree>
</xsl:template>

<xsl:template match="NODES">
	<xsl:apply-templates select="NODE"/>
</xsl:template>

<xsl:template match="NODE">
<branch>
	<attribute name="name">
		<xsl:attribute name="value">
			<xsl:value-of select="NAME"/>
		</xsl:attribute>
	</attribute>
	
	<attribute name="id">
		<xsl:attribute name="value">
			<xsl:value-of select="@ID"/>
		</xsl:attribute>
	</attribute> 
	
	<attribute name="extinct">
		<xsl:attribute name="value">
			<xsl:value-of select="@EXTINCT"/>
		</xsl:attribute>		
	</attribute>
	
	<attribute name="italicizename">
		<xsl:attribute name="value">
			<xsl:value-of select="@ITALICIZENAME"/>
		</xsl:attribute>
	</attribute>
	
	<!-- recursive call to the subtree -->
	<xsl:apply-templates select="NODES"/>
</branch>
</xsl:template>

</xsl:stylesheet>