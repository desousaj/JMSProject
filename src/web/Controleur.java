package web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import metier.Inscription;

/**
 * Definition of the two JMS destinations used by the quickstart (one queue and
 * one topic).
 */
/*
 @JMSDestinationDefinitions
 (value = {
 @JMSDestinationDefinition(
 name = "java:jboss/exported/topic/DemandeInscriptionJmsTopic", 
 interfaceName = "javax.jms.Topic", 
 destinationName = "DemandeInscriptionJmsTopic")

 }

 )
 */
/**
 * Servlet implementation class Traitement
 */
@WebServlet("/Controleur")
public class Controleur extends HttpServlet {
	private static final long serialVersionUID = 10L;
	private static final String ACTION_TYPE = "action";
	private static final String AFFICHER_INSCRIPTION = "afficheInscriptions";
	private static final String AJOUTER_INSCRIPTION = "ajouteInscription";
	private static final String ENVOI_INSCRIPTION = "envoiInscription";
	private static final String RETOUR_ACCUEIL = "Retour";

	/**
	 * @see HttpServlet#HttpServlet()
	 */

	@Resource(lookup = "java:jboss/exported/topic/DemandeInscriptionJmsTopic")
	private Topic topic;
	// On accède à l'EJB

	@Resource(mappedName = "java:/ConnectionFactory")
	private TopicConnectionFactory cf;

	// Session établie avec le serveur
	private TopicSession session = null;

	// Le client utilise un Producteur de messsage pour envoyer une demande de
	// formation
	private TopicPublisher producer;

	/**
	 * Constructeur par défaut de la classe
	 */
	public Controleur() {
		super();
	}

	/**
	 * 
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("fgjrekhgrekuhgrekhgkrehghregreljgjrejgre");
		try {
			TraiteRequete(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("klfjrlikfjergrekgjrelmjgmlrekjgmlorjeolmgjremlo");
		// TODO Auto-generated method stub
		try {
			TraiteRequete(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Procédure principale de démarrage
	 */
	public void TraiteRequete(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		// On récupère l'action
		String actionName = request.getParameter(ACTION_TYPE);

		// Si on veut afficher l'ensemble des demandes d'inscription
		if (AJOUTER_INSCRIPTION.equals(actionName)) {

			request.getRequestDispatcher("PostMessage.jsp").forward(request,
					response);

		} else

		if (AFFICHER_INSCRIPTION.equals(actionName)) {

			Inscription unedemande = new Inscription();
			ArrayList<Inscription> listeDmdInscription;

			try {
				// On récupère la liste des demandes d'inscription
				listeDmdInscription = unedemande.recupererDmdInscription();
				// On fixe l'attribut correspondant à cette liste
				request.setAttribute("listeDmdInscription", listeDmdInscription);
				// On fixe l'attribut correspondant au nombre total de demandes
				// d'inscription
				request.setAttribute("nbInscription",
						listeDmdInscription.size());
				this.getServletContext()
						.getRequestDispatcher("/AfficheInscriptions.jsp")
						.include(request, response);
			} catch (Exception e) {
				// On passe l'erreur à la page JSP
				System.out.println("Erreur client  :" + e.getMessage());
				request.setAttribute("MesErreurs", e.getMessage());
				request.getRequestDispatcher("Affichage.jsp").forward(request,
						response);
			}

		} else if (RETOUR_ACCUEIL.equals(actionName)) {
			this.getServletContext().getRequestDispatcher("/index.jsp")
					.include(request, response);
		}

		else if (ENVOI_INSCRIPTION.equals(actionName))

		{
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			response.setContentType("text/html;charset=UTF-8");
			response.setContentType("text/html;charset=UTF-8");

			// On récupère le nom et le prénom saisis
			String nom = request.getParameter("nom");
			String prenom = request.getParameter("prenom");

			if ((nom != null) && (prenom != null)) {
				try {
					// On récupère la valeur des autres champs saisis par
					// l'utilisateur
					String datenaissance = request
							.getParameter("datenaissance");
					Date unedate = sdf.parse(datenaissance);
					String chn = unedate.toString();
					String adresse = request.getParameter("adresse");
					String cpostal = request.getParameter("cpostal");
					String ville = request.getParameter("ville");

					// On crée une demande d'inscription avec ces valeurs
					Inscription unedemande = new Inscription();
					unedemande.setNomcandidat(nom);
					unedemande.setPrenoncandidat(prenom);
					unedemande.setDatenaissance(unedate);
					unedemande.setAdresse(adresse);
					unedemande.setCpostal(cpostal);
					unedemande.setVille(ville);

					// On envoie cette demande d'inscription dans le topic
					boolean ok = envoi(unedemande);

					// On retourne à la page d'accueil
					this.getServletContext().getRequestDispatcher("/index.jsp")
							.include(request, response);
				} catch (Exception e) {
					// On passe l'erreur à la page JSP
					System.out.println("Erreur client  :" + e.getMessage());
					request.setAttribute("MesErreurs", e.getMessage());
					request.getRequestDispatcher("PostMessage.jsp").forward(
							request, response);
				} /*
				 * catch (Exception e) {
				 * System.out.println("Erreur client Exception   :" +
				 * e.getMessage()); request.setAttribute("MesErreurs",
				 * e.getMessage());
				 * 
				 * request.getRequestDispatcher("PostMessage.jsp").forward(
				 * request, response); }
				 */

			}
		}
	}

	/**
	 * Permet de publier une demande d'inscription dans le topic
	 * 
	 * @param unedemande
	 *            La demande d'inscription à publier
	 * @return
	 * @throws Exception
	 */
	boolean envoi(Inscription unedemande) throws Exception {

		TopicConnection connection = null;

		try {

			// On crée la connexion JMS , session, producteur et message;
			/*
			 * connection = connectionFactory.createConnection(
			 * System.getProperty("username", DEFAULT_USERNAME),
			 * System.getProperty("password", DEFAULT_PASSWORD));
			 */

			// Création Connection et Session JMS
			connection = cf.createTopicConnection();
			session = connection.createTopicSession(false,
					Session.AUTO_ACKNOWLEDGE);

			// On crée le producteur utilisé pour envoyer un message
			producer = session.createPublisher(topic);
			// On lance la connection
			connection.start();
			ObjectMessage message = session.createObjectMessage();
			message.setObject(unedemande);

			// On publie le message
			producer.publish(message);
			producer.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Fermeture de la connection , Sinon Jboss Messaging pas content.
			if (connection != null) {
				session.close();
				connection.close();
			}
		}
		return true;
	}

}
