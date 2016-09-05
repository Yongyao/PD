package PlanetaryDefense.PD.kb;

import java.util.List;

import PlanetaryDefense.PD.driver.ESdriver;
import edu.knowitall.openie.Argument;
import edu.knowitall.openie.Instance;
import edu.knowitall.openie.OpenIE;
import edu.knowitall.tool.parse.ClearParser;
import edu.knowitall.tool.postag.ClearPostagger;
import edu.knowitall.tool.srl.ClearSrl;
import edu.knowitall.tool.tokenize.ClearTokenizer;
import scala.collection.JavaConversions;
import scala.collection.Seq;

public class PDopenIE {

	public PDopenIE() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("---Started---");

		ESdriver esd = new ESdriver();

		OpenIE openIE = new OpenIE(new ClearParser(new ClearPostagger(new ClearTokenizer())), new ClearSrl(), false, false);

		Seq<Instance> extractions = openIE.extract("Barack Obama lives in America. He works for the Federal Goverment.");

		List<Instance> list_extractions = JavaConversions.seqAsJavaList(extractions);
		for(Instance instance : list_extractions) {
			StringBuilder sb = new StringBuilder();

			sb.append(instance.confidence())
			.append('\t')
			.append(instance.extr().context())
			.append('\t')
			.append(instance.extr().arg1().text())
			.append('\t')
			.append(instance.extr().rel().text())
			.append('\t');

			List<Argument> list_arg2s = JavaConversions.seqAsJavaList(instance.extr().arg2s());
			for(Argument argument : list_arg2s) {
				sb.append(argument.text()).append("; ");
			}

			System.out.println(sb.toString());
		}
		try {
			esd.closeES();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
