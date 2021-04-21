enablePlugins(org.nlogo.build.NetLogoExtension)

netLogoExtName := "export-the"

netLogoClassManager := "org.nlogo.extension.exportthe.ExportTheExtension"

netLogoZipSources := false

version := "1.0.2"

scalaVersion := "2.12.12"

scalaSource in Compile := baseDirectory.value / "src"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  // We're using a deprecated method `getPlot()` and others but there is no way to surpress the errors for just
  // that usage in Scala 2.12.12 (there is a `nowarn` annotation in Scala 2.13.3).  We can re-enable
  // this after the deprecated usage is removed (NetLogo API 6.2?) or if we bump to Scala 2.13.3+.
  // -Jeremy B November 2020
  //"-Xfatal-warnings",
  "-encoding",
  "us-ascii"
)

// The remainder of this file is for options specific to bundled netlogo extensions
// if copying this extension to build your own, you need nothing past line 14 to build
// sample-scala.zip
netLogoTarget :=
  org.nlogo.build.NetLogoExtension.directoryTarget(baseDirectory.value)

resolvers      += "netlogo" at "https://dl.cloudsmith.io/public/netlogo/netlogo/maven/"
netLogoVersion := "6.2.0-d27b502"
