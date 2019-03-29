package org.nlogo.extension.exportthe

import java.io.{ ByteArrayOutputStream, CharArrayWriter, PrintWriter }
import java.util.{ Base64, StringTokenizer }
import javax.imageio.ImageIO

import scala.concurrent.{ Await, Promise }
import scala.concurrent.duration.Duration
import scala.util.Try

import org.nlogo.api.{ Argument, Context, DefaultClassManager, Dump, ExtensionException, PrimitiveManager, Reporter }
import org.nlogo.app.{ App, ModelSaver }
import org.nlogo.app.interfacetab.{ WidgetPanel, WidgetWrapper }
import org.nlogo.awt.EventQueue
import org.nlogo.core.{ Model, Syntax }
import org.nlogo.headless.HeadlessWorkspace
import org.nlogo.plot.{ PlotExporter, PlotManager }
import org.nlogo.window.{ GUIWorkspace, InterfacePanelLite, OutputWidget }

class ExportTheExtension extends DefaultClassManager {

  override def load(manager: PrimitiveManager): Unit = {
    manager.addPrimitive("model" , ModelPrim)
    manager.addPrimitive("output", OutputPrim)
    manager.addPrimitive("plot"  , PlotPrim)
    manager.addPrimitive("view"  , ViewPrim)
    manager.addPrimitive("world" , WorldPrim)
  }

  private object ModelPrim extends Reporter {
    override def getSyntax = Syntax.reporterSyntax(ret = Syntax.StringType)
    override def report(args: Array[Argument], context: Context): AnyRef = {
      if (!context.workspace.isHeadless) {
        import org.nlogo.fileformat.basicLoader
        val model = new ModelSaver(App.app, null).currentModelInCurrentVersion
        basicLoader.sourceString(model, "nlogo").getOrElse(throw new ExtensionException("Model could not be serialized"))
      } else {
        throw new ExtensionException("Models may only be exported from the GUI")
      }
    }
  }

  private object OutputPrim extends Reporter {
    override def getSyntax = Syntax.reporterSyntax(ret = Syntax.StringType)
    override def report(args: Array[Argument], context: Context): AnyRef = {
      context.workspace match {
        case hlWS: HeadlessWorkspace =>
          hlWS.outputAreaBuffer.toString

        case guiWS: GUIWorkspace =>

          val owOpt =
            guiWS.getWidgetContainer match {
              case wp: WidgetPanel =>
                wp.getComponents.collect { case w: WidgetWrapper => w }.map(_.widget).collect {
                  case ow: OutputWidget => ow
                }.headOption
              case ipl: InterfacePanelLite =>
                ipl.getComponents.collect { case ow: OutputWidget => ow }.headOption
              case x =>
                throw new ExtensionException(s"Unsupported widget container type: ${x.getClass.getName}")
            }

          owOpt.map(_.outputArea.valueText).getOrElse("")

        case x =>
          throw new ExtensionException(s"Unsupported workspace type: ${x.getClass.getName}")

      }
    }
  }

  private object PlotPrim extends Reporter {
    override def getSyntax = Syntax.reporterSyntax(ret = Syntax.StringType, right = List(Syntax.StringType))
    override def report(args: Array[Argument], context: Context): AnyRef = {

      val plotName    = args(0).getString
      val plotManager = context.workspace.plotManager.asInstanceOf[PlotManager]
      val plotOpt     = Option(plotManager.getPlot(plotName))

      plotOpt.map {
        plot =>
          val caw = new CharArrayWriter
          new PlotExporter(plot, Dump.csv).export(new PrintWriter(caw))
          caw.toString
      }.getOrElse(throw new ExtensionException(s"No such plot: $plotName"))

    }
  }

  private object ViewPrim extends Reporter {
    override def getSyntax = Syntax.reporterSyntax(ret = Syntax.StringType)
    override def report(args: Array[Argument], context: Context): AnyRef = {

      val image = context.workspace.exportView
      val baos  = new ByteArrayOutputStream
      ImageIO.write(image, "png", baos)
      baos.flush()
      val bytes = baos.toByteArray
      baos.close()

      s"data:image/png;base64,${Base64.getEncoder.encodeToString(bytes)}"

    }
  }

  private object WorldPrim extends Reporter {
    override def getSyntax = Syntax.reporterSyntax(ret = Syntax.StringType)
    override def report(args: Array[Argument], context: Context): AnyRef = {

      val exportTheWorld = {
        () =>
          val caw = new CharArrayWriter
          context.workspace.exportWorld(new PrintWriter(caw))
          caw.toString
      }

      if (!context.workspace.isHeadless) {
        val promise = Promise[String]()
        EventQueue.invokeLater { () => promise.complete(Try(exportTheWorld())) }
        Await.result(promise.future, Duration.Inf)
      } else {
        exportTheWorld()
      }

    }
  }

}
