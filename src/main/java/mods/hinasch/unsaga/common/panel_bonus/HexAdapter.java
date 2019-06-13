package mods.hinasch.unsaga.common.panel_bonus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.realmsclient.util.Pair;

import mods.hinasch.lib.misc.XY;
import mods.hinasch.unsaga.skillpanel.ISkillPanel;
import mods.hinasch.unsaga.skillpanel.PanelBonus;
import mods.hinasch.unsaga.skillpanel.SkillPanelAPI;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class HexAdapter {

	public static final List<Hex> HEXES;

	/** ヘックスの周囲を取得する時に使う相対座標*/
	public static final List<XY> AROUND = Lists.newArrayList(new XY(-2,0),new XY(2,0),new XY(-1,-1),new XY(1,-1),new XY(-1,1),new XY(1,1));


	static{
		List<Hex> builder = Lists.newArrayList();
		builder.add(new Hex(1,0,0));
		builder.add(new Hex(3,0,1));
		builder.add(new Hex(0,1,2));
		builder.add(new Hex(2,1,3));
		builder.add(new Hex(4,1,4));
		builder.add(new Hex(1,2,5));
		builder.add(new Hex(3,2,6));
		HEXES = ImmutableList.copyOf(builder);
	}


	final IInventory inv;
	public HexAdapter(IInventory inventory){
		this.inv = inventory;
	}


	/** ヘックスを取得。1,0/3,0/0,1/2,1…という座標方式を適用、
	 * 有効な座標でなかったらEmpty*/
	public Hex getHex(int x,int y){
		return HEXES.stream()
				.filter(in -> in.position.equals(new XY(x,y)))
				.findFirst()
				.orElse(Hex.NULL_HEX);
	}

	public IInventory getInventory(){
		return inv;
	}

	public ItemStack getStackFrom(Hex hex){
		return this.getInventory().getStackInSlot(hex.getSlotNumber());
	}

	public boolean isHexEmpty(Hex hex){
		return this.getPanel(hex)==SkillPanels.DUMMY;
	}
	public ISkillPanel getPanel(Hex hex){
		return Optional.of(hex)
				.filter(in -> !in.isNullHex())
				.filter(in -> !this.getStackFrom(in).isEmpty())
				.map(in ->SkillPanelAPI.getSkillPanel(this.getStackFrom(in))
						.orElse(SkillPanels.DUMMY))
				.orElse(SkillPanels.DUMMY);
	}


	/** そのヘックスを囲んでいるヘックスを取得*/
	public List<Hex> getAroundHexes(Hex hex){
		return AROUND.stream()
				.map(in -> hex.getPosition().add(in))
				.filter(in -> !this.getHex(in.getX(), in.getY()).isNullHex())
				.map(in -> this.getHex(in.getX(), in.getY()))
				.collect(Collectors.toList());
	}

	public boolean isSamePanel(Hex base,Hex... hexesIn){
		return Optional.ofNullable(hexesIn)
				.filter(in -> !this.isHexEmpty(base))
				.filter(in -> !this.getPanel(base).isEmpty())
				.map(hexes -> Lists.newArrayList(hexesIn).stream()
						.allMatch(hex -> this.getPanel(base).iconType()==(this.getPanel(hex)).iconType()))
				.orElse(false);

	}
	public PanelJointList<IPanelJoint> findTriangles(){
		PanelJointList.Builder<IPanelJoint> triangles = new PanelJointList.Builder();
		if(this.isSamePanel(this.getHex(1, 0),this.getHex(4, 1),this.getHex(1, 2))){
			triangles.addJoint(new Triangle(this.getHex(1, 0),this.getHex(4,1),this.getHex(1, 2)));
		}
		if(this.isSamePanel(this.getHex(3, 0),this.getHex(0, 1),this.getHex(3, 2))){
			triangles.addJoint(new Triangle(this.getHex(3, 0),this.getHex(0,1),this.getHex(3, 2)));
		}

		return triangles.build().sort().cleanCollisions();
	}
	public PanelJointList<IPanelJoint> findLines(){
		PanelJointList.Builder<IPanelJoint> lines = new PanelJointList.Builder();
		if(this.isSamePanel(this.getHex(1, 0),this.getHex(2, 1),this.getHex(3, 2))){
			lines.addJoint(new Line(this.getHex(1, 0),this.getHex(2,1),this.getHex(3, 2)));
		}
		if(this.isSamePanel(this.getHex(3, 0),this.getHex(2, 1),this.getHex(1, 2))){
			lines.addJoint(new Line(this.getHex(3, 0),this.getHex(2,1),this.getHex(1, 2)));
		}
		if(this.isSamePanel(this.getHex(0, 1),this.getHex(2, 1),this.getHex(4, 1))){
			lines.addJoint(new Line(this.getHex(0, 1),this.getHex(2,1),this.getHex(4, 1)));
		}
		return lines.build().sort().cleanCollisions();
	}
	private PanelJointList<IPanelJoint> findJoints(Hex base){
		return this.getAroundHexes(base).stream()
				.filter(in -> this.isSamePanel(base, in))
				.map(in ->Pair.of(base,in))
				.map(Jointed::of)
				.collect(PanelJointList.COLLECTOR).build();
	}

	public PanelBonus getPanelBonus(Hex hex,int multiply){
		return this.getPanel(hex).panelBonus().multiplyBy(multiply);
	}
	public PanelJointList<IPanelJoint> findJoints(){

		return HEXES.stream()
				.flatMap(in -> this.findJoints(in).asList().stream())
				.collect(PanelJointList.COLLECTOR)
				.build()
				.sort()
				.cleanCollisions();
	}

	/** トライアングル・ライン・ジョイントを含む全てのボーナス配置を含むリストを作成する
	 * （重複消去、ソート済）*/
	public PanelJointList<IPanelJoint> buildJointList(){
		//		PanelJointList.Builder<IPanelJoint> jointListBuilder = new PanelJointList.Builder();
		//		PanelJointList<IPanelJoint> triangles = this.findTriangles();
		//		PanelJointList<IPanelJoint> lines = this.findLines();
		//		PanelJointList<IPanelJoint> joints = this.findJoints();
		//各ジョイントを全てまとめて突っ込む
		return Stream.empty()
				.flatMap(in -> this.findTriangles().asList().stream())
				.flatMap(in -> this.findLines().asList().stream())
				.flatMap(in -> this.findJoints().asList().stream())
				.collect(PanelJointList.COLLECTOR)
				.build()
				.sort()
				.cleanCollisions();
		//		triangles.joints.stream().map(in ->(IPanelJoint)in).forEach(in -> jointListBuilder.addJoint(in));
		//		lines.joints.stream().map(in ->(IPanelJoint)in).forEach(in -> jointListBuilder.addJoint(in));
		//		joints.joints.stream().map(in ->(IPanelJoint)in).forEach(in -> jointListBuilder.addJoint(in));
		//		return jointListBuilder.build().sort().cleanCollisions();
	}

	public PanelBonusSummary createSummary(){
		PanelBonusSummary summary = new PanelBonusSummary();
		PanelJointList<IPanelJoint> allJoints = this.buildJointList();
		Map<IPanelJoint,PanelBonus> bonusList = new HashMap<>();
		for(IPanelJoint layout:allJoints.asList()){
			layout.hexes().forEach(in ->{
				summary.add(in, layout, this.getPanelBonus(in, layout.getBonusMultiply()));
			});
		}
		return summary;
	}
}
