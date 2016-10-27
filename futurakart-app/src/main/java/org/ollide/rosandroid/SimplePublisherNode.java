/*
 * Copyright (C) 2014 Oliver Degener.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ollide.rosandroid;

import android.util.Log;

import org.gobgob.map2d.Map2dView;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import geometry_msgs.Pose2D;

public class SimplePublisherNode extends AbstractNodeMain implements NodeMain, Map2dView.OnGoalUpdatedListener{

    private static final String TAG = SimplePublisherNode.class.getSimpleName();
    Publisher<Pose2D> mPublisher;
    Subscriber<Pose2D> mSubscriber;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("AndroidCommand/KartGoal");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {

        mPublisher = connectedNode.newPublisher(GraphName.of("KartGoal"), geometry_msgs.Pose2D._TYPE);
        //mSubscriber = connectedNode.newSubscriber(GraphName.of("KartGoal"), geometry_msgs.Pose2D._TYPE);

    }

    public void onNewPos(float x, float y) {
    }

        @Override
    public void onGoalUpdated(float x, float y) {

        Log.i(TAG, String.format("publishing goal: %f, %f", x, y));

        geometry_msgs.Pose2D goal = mPublisher.newMessage();
        goal.setX(x);
        goal.setY(y);
        goal.setTheta(0); // TODO specify theta

        mPublisher.publish(goal);
    }
}
