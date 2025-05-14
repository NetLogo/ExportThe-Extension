enablePlugins(org.nlogo.build.NetLogoExtension)

netLogoVersion      := "7.0.0-beta1-2bad0d8"
netLogoExtName      := "export-the"
netLogoClassManager := "org.nlogo.extension.exportthe.ExportTheExtension"
version             := "1.0.2"
scalaVersion        := "2.13.16"

Compile / scalaSource := baseDirectory.value / "src" / "main"
Test    / scalaSource := baseDirectory.value / "src" / "test"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Xfatal-warnings",
  "-encoding",
  "us-ascii"
)
