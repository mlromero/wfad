package cl.uchile.cos.sync.adapters;

import java.io.Serializable;

import cl.uchile.cos.sync.Message;

public interface Adapter extends Serializable{
	/**
	 * M&eacute;todo utilizado por el servidor para enviar un mensaje al cliente.
	 * 
	 * @param message
	 */
	public void sendToClient(Message message); 

	/**
	 * Envia un mensaje al servidor para que sea procesado. Si el método retorna
	 * false el cliente debe ignorar el evento que genero la llamada.
	 * 
	 * @param message
	 * @return true si el mensaje es valido, false si no. Si es mensaje tipo
	 *         ESTADO siempre retorna true.
	 */
	public boolean sendToServer(Message message);

	/**
	 * Indica que el objeto representado por objectId esta acoplado desde ahora a través de este adaptador.
	 * @param objectId
	 */
	public void couple(String objectId);
	/**
	 * Indica que el objeto representado por objectId esta acoplado desde ahora a través de este adaptador.
	 * Adeamas recibe una seria de mensajes que representan el estado actual del objeto en el cliente.
	 * @param objectId
	 */
	public void couple(String objectId, Message... state);

	/**
	 * Indica que el objeto representado por objectId ya no esta acoplado a través de este adaptador.
	 * @param objectId
	 */
	public void decouple(String objectId);

	/**
	 * Desacopla todos los objetos antes acoplados por este adaptador.
	 */
	public void decoupleAll();
	
	/**
	 * Indica si esta desocupado, es decir, si ya envio todos los mensajes que tenia que enviar.
	 * @return
	 */
	public boolean ready(); 

}
