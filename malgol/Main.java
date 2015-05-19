package malgol;

import malgol.lexer.Lexer;
import malgol.parser.Parser;
import malgol.node.Start;
import malgol.ast.Program;
import malgol.transform.*;

import java.io.*;

public class Main {

	private static PrintWriter out = null;
	private static Reader in = null;
	private static int phase = -1;

	private static void usageError() {
		System.err.println("usage: java Main phase inputFile [outputFile]");
		System.err.println("phase must be an integer between 0 and 5");
		System.exit(1);
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 2 || args.length > 3)
			usageError();
		try {
			phase = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			usageError();
		}
		if (phase < 0 || phase > 5)
			usageError();
		try {
			in = new FileReader(args[1]);
			if (args.length > 2)
				out = new PrintWriter(args[2]);
			else
				out = new PrintWriter(new OutputStreamWriter(System.out));

			/* Form our AST */
			Lexer lexer = new Lexer(new PushbackReader(in, 1024));
			Parser parser = new Parser(lexer);
			Start ast = parser.parse();

			ParseTree2ASTVisitor toAST = new ParseTree2ASTVisitor();
			ast.apply(toAST);
			Program program = toAST.getResult();
			doPhases(program);

		} catch (FileNotFoundException e) {
			if (in == null) {
				System.err.println("Cannot open input file: " + args[1]);
				System.exit(-1);
			} else if (out == null) {
				System.err.println("Cannot open output file: " + args[2]);
				System.exit(-1);
			} else {
				System.err.println(e);
			}
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	private static void doPhases(Program program) {

		if (phase == 1) {
			UntypedAST2CVisitor UtoC = new UntypedAST2CVisitor();
			UtoC.clear();
			program.accept(UtoC);
			out.println(UtoC.getCode());
		}

		if (phase >= 2) {
			StaticAnalysisVisitor saVisit = new StaticAnalysisVisitor();
			program.accept(saVisit);

			if (phase == 2) {
				AST2CVisitor toC = new AST2CVisitor();
				toC.clear();
				program.accept(toC);
				out.println(toC.getCode());
			}
		}

		if (phase >= 3) {
			RemoveStructuredControlVisitor rscv = new RemoveStructuredControlVisitor();
			program.accept(rscv);
			program = rscv.getResult();
			if (phase == 3) {
				NonStructuredAST2CVisitor toC2 = new NonStructuredAST2CVisitor();
				toC2.clear();
				program.accept(toC2);
				out.println(toC2.getCode());
			}
		}

		if (phase >= 4) {
			SimplifyExpressionsVisitor sev = new SimplifyExpressionsVisitor();
			program.accept(sev);
			program = sev.getResult();
			if (phase == 4) {
				LinearExpressionAST2CVisitor toC2 = new LinearExpressionAST2CVisitor();
				toC2.clear();
				program.accept(toC2);
				out.println(toC2.getCode());
			}
		}

		if (phase == 5) {
			FrameLayoutVisitor flv = new FrameLayoutVisitor();
			program.accept(flv);
			CodeGenerationVisitor cgv = new CodeGenerationVisitor(flv.getLayoutInformation());
			program.accept(cgv);
			out.println(cgv.getCode());
		}
	}
}