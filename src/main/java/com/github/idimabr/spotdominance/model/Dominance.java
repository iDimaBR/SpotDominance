package com.github.idimabr.spotdominance.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@Getter @Setter
public class Dominance {

    private String id;
    private String region;
    private String name;
    private int minMembers;
    private int rewardTime;
    private int domineTime;
    private int disputeTime;
    private int counterReward;
    private int counterDomine;
    private int counterDispute;
    private List<String> rewards;

    private String clan;

    public void counting(){
        this.counterDomine--;
        this.counterReward--;

        if(this.counterDomine < 0){
            this.counterDomine = this.domineTime;
        }
        if(this.counterReward < 0){
            this.counterReward = this.rewardTime;
        }
    }

    public void dispute(){
        this.counterDispute--;
        if(this.counterDispute == 0){
            this.counterDispute = this.disputeTime;
        }
    }

}
