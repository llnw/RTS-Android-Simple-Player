#### Jump to a section
[ Setup ](#setup) | 
[ API ](#api) | 


<a name="setup"></a>
# Setup

1. Open Project in Android Studio

2. Connect physical Android device to your computer, and select it in "Run" dropdown at top.

3. Wait for Android Studio to sync the project files

4. Click Green run arrow to build and run test app.

<a name="api"></a>
# API

#### public void setConnectionListener(LLConnectionListener listener)
Set connection listener object containing the following available callbacks:
* onStreamNotFound()
   * Called when 404 received from discovery endpoint.
* onConnectSuccess()
   * Called when websocket successfully connects.
* onConnectFailure()
   * Called when websocket fails to connect.
* onDisconnect()
   * Called when peer connection is closed after an unsubscribe.
* onSubscribeStart()
   * Called when subscribe is called.
* onSubscribeFail()
   * Called when subscribe fails.
* onSubscribeInvalidHost()
   * // TODO Not currently called
* onSubscribeInvalidName()
   * // TODO Not currently called 
* onSubscribeStop()
* onSubscribeMetadata()
   * // TODO Not currently called
* onSubscribeSendInvoke()
   * // TODO Not currently called
* onSubscribePlayUnpublish()
   * // TODO Not currently called
* onSubscribeConnectionClosed()
   * // TODO Not currently called
* onSubscribeOrientationChange()
   * // TODO Not currently called
* onSubscribeVolumeChange()
   * // TODO Not currently called
* onSubscribeTimeUpdate()
   * // TODO Not currently called
* onSubscribePlaybackChange()
   * // TODO Not currently called
* onSubscribeFullScreenChange()
   * // TODO Not currently called
* onSubscribeAutoplayFailure()
   * // TODO Not currently called
* onSubscribeAutoplayMuted()
   * // TODO Not currently called
* onWebRTCPeerConnectionAvailable()
   * Called when peer connection is established.
* onWebRTCOfferStart()
   * Called when WebRTC offer is first being generated by client.
* onWebRTCOfferEnd()
   * Called when WebRTC offer generation is complete.
* onWebRTCAnswerStart()
   * Called when WebRTC answer is received by client.
* onWebRTCAnswerEnd()
   * Called when WebRTC remote description is set or setting of remote description fails.
* onWebRTCCandidateStart()
   * Called when local description is set and as local candidates are about to begin.
* onWebRTCCandidateEnd()
   * Called when local WebRTC locals candidates are ready and signalling state is table.
* onWebRTCIceTrickleComplete()
   * Called when Ice Connection State is "Completed"
* onProfilesReceived(ArrayList<String> profiles)
   * Called when list of active profiles is received from server.

#### public void setDataChanelListener(DataChannelListener listener)
Set data channel listener with the following available callbacks:
* onDataObjectMessage(LLDataMessage msg)
   * Callback for a received data message.
* onDataObjectUpdateResponse(LLUpdateResponseMessage msg)
   * Callback for a received update to the data object.
* onDataObjectBroadcast(LLDataBroadcastMessage msg)
   * Callback for a receied broadcast message.
#### public void stop()
#### public void play()
#### public void mute()
#### public void unmute()
#### sendDataMessage(String text)
Broadcasts a message on the data channel.
#### setVariant(String constraint)
Requests the specified variant from the backend.
#### public String getVariant()
Returns the current playback variant.
#### public ArrayList getVariant()
Returns the available profiles
#### public void subscribe(boolean iceRestart)
Subscribes to the stream specified in the configuration object. Specify `iceRestart = true` if only restarting ICE.
#### public void unsubscribe(boolean iceRestart)
Unsubscribes from the stream. Specify `iceRestart = true` if only unsubscribing to restart ICE.
#### public MediaStream getMediaStream()
Returns the native playback [MediaStream](https://chromium.googlesource.com/external/webrtc/+/master/sdk/android/api/org/webrtc/MediaStream.java) object.
#### public PeerConnection getPeerConnection()
Returns the native [PeerConnection](https://chromium.googlesource.com/external/webrtc/+/master/sdk/android/api/org/webrtc/PeerConnection.java)
#### public void setVolume(int volume)
Sets volume (1-10)
#### public void dataObjectUpdate(ArrayList keys, String value, boolean failIfKeyMissing)
Update key-value pair in data object.
#### public void dataObjectAdd(ArrayList keys, String value, boolean failIfKeyPresent)
Add item to list in key.
#### public void dataObjectDelete(ArrayList keys, boolean failIfKeyMissing)
Delete key-value pair from data object.
#### public void dataObjectInc (ArrayList<String> keys, double increment, String senderRef, boolean createIfKeyMissing)
Increment kay-value pair.
#### public void dataObjectDec(ArrayList<String> keys, double increment, String senderRef, boolean createIfKeyMissing)
Decrement key-value pair.
#### public void dataObjectCAS(ArrayList keys, String compare, String swap, boolean createIfKeyMissing, String senderRef)
Compare and swap key-value pair. (Replace value if equal to compare param)
