enablePlugins(org.nlogo.build.NetLogoExtension)

netLogoVersion      := "7.0.0-beta1"
netLogoExtName      := "export-the"
netLogoClassManager := "org.nlogo.extension.exportthe.ExportTheExtension"
version             := "1.1.0"
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
  "17"
)
