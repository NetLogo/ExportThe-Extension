import org.nlogo.build.NetLogoExtension

enablePlugins(NetLogoExtension)

netLogoVersion      := "7.1.0-alpha1-931b371"
netLogoExtName      := "export-the"
netLogoClassManager := "org.nlogo.extension.exportthe.ExportTheExtension"
version             := "1.1.2"
scalaVersion        := "3.7.0"

Compile / scalaSource := baseDirectory.value / "src" / "main"
Test    / scalaSource := baseDirectory.value / "src" / "test"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding",
  "us-ascii",
  "-release",
  "21",
  "-Wunused:linted"
)
