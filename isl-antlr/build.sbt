
ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "3.2.0"

lazy val root = (project in file("."))
  .enablePlugins(Antlr4Plugin)
  .settings(
    name := "icfpc2022-isl-antlr",
    Antlr4 / antlr4Version := "4.10.1",
    Antlr4 / antlr4PackageName := Some("isl.antlr4"),
    Antlr4 / antlr4GenListener := true,
    Antlr4 / antlr4GenVisitor := true,
    Antlr4 / antlr4TreatWarningsAsErrors := true
  )

