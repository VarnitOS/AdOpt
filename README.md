# Ad Optimization Platform (AdOpt)

A sophisticated real-time bidding (RTB) optimization platform that uses game theory principles to optimize advertising campaigns and maximize ROI.

## Project Structure

The project is divided into two main components:

### Backend (Java + Spring Boot)

The backend provides the core optimization engine with the following components:

- **BidOptimizationService**: Core service that uses game theory models to calculate optimal bids
- **CompetitorAnalysisService**: Analyzes and tracks competitor behavior in RTB auctions
- **PredictionService**: Predicts click-through rates, conversion rates, and other metrics
- **UserProfileService**: Manages user profile data for targeting
- **CampaignService**: Manages ad campaigns and bid responses

### Frontend (Next.js + TypeScript + Material UI)

The frontend provides a dashboard for campaign management and optimization insights:

- **Campaign Performance**: View and track campaign metrics
- **Bidding Simulator**: Simulate bid outcomes based on game theory models
- **Competitor Analysis**: Analyze competitor bidding behavior
- **Game Theory Insights**: Get strategic insights based on game theory models

## Key Features

- **Game Theory-Based Optimization**: Uses Nash Equilibrium models to find optimal bidding strategies
- **Competitor Analysis**: Tracks and analyzes competitor bidding patterns
- **Machine Learning Predictions**: Predicts CTR, CVR, and user value
- **Real-time Bidding**: Supports real-time bidding protocols
- **Interactive Dashboard**: Visualizes campaign performance and optimization insights

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- Node.js 14+ and npm 7+
- Git

### Backend Setup

1. Clone the repository: `git clone https://github.com/VarnitOS/AdOpt.git`
2. Navigate to the `backend` directory: `cd AdOpt/backend`
3. Run `mvn clean install` to build the project
4. Run `mvn spring-boot:run` to start the server
5. The backend server will be available at `http://localhost:8080`

**Troubleshooting Backend Issues:**
- If you encounter Lombok-related errors, ensure your IDE has Lombok plugin installed and annotation processing enabled
- For "Cannot find symbol" errors in model classes, try rebuilding with `mvn clean install -U` to update dependencies

### Frontend Setup

1. Navigate to the `frontend` directory: `cd ../frontend`
2. Run `npm install` to install dependencies
3. Run `npm run dev` to start the development server
4. Open [http://localhost:3000](http://localhost:3000) in your browser

**Troubleshooting Frontend Issues:**
- If you encounter module resolution errors, try clearing the Next.js cache with `npm run clean`
- For dependency-related issues, ensure you're using the correct Node.js version

### Running the Entire Stack

The repository includes a convenience script to run both frontend and backend:

```bash
chmod +x start-dev.sh
./start-dev.sh
```

## API Documentation

The backend exposes the following main API endpoints:

- `GET /api/campaigns`: Get all campaigns
- `GET /api/campaigns/{id}`: Get a specific campaign
- `POST /api/campaigns`: Create a new campaign
- `PUT /api/campaigns/{id}`: Update a campaign
- `POST /api/bid`: Generate an optimal bid for a given bid request
- `POST /api/bid/{id}/win`: Process auction win notification
- `POST /api/bid/{id}/loss`: Process auction loss notification

## Technologies Used

- **Backend**: Java, Spring Boot, Spring Data JPA, H2 Database
- **Frontend**: Next.js, TypeScript, Material UI, Chart.js
- **Machine Learning**: DeepLearning4j
- **Game Theory**: Custom Nash Equilibrium implementation

## Deployment

### Backend Deployment

The backend can be deployed as a standard Spring Boot application:

1. Build the JAR: `mvn clean package`
2. Run the JAR: `java -jar target/adopt-backend-0.1.0.jar`

For production deployment, consider using:
- Docker containers
- AWS Elastic Beanstalk
- Heroku Java deployment

### Frontend Deployment

The Next.js frontend can be deployed using:

1. Build the production bundle: `npm run build`
2. Start the production server: `npm start`

Recommended hosting options:
- Vercel (optimized for Next.js)
- Netlify
- AWS Amplify

## Project Status

AdOpt is currently in active development. The platform provides core functionality for RTB optimization using game theory principles, but several features are still being refined:

- [x] Core bidding optimization engine
- [x] Basic dashboard with campaign metrics
- [x] Competitor analysis visualization
- [ ] Advanced machine learning prediction models
- [ ] A/B testing framework
- [ ] Budget pacing algorithms
- [ ] Multi-account management

## Contributing

We welcome contributions to the AdOpt platform! Here's how you can contribute:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to your branch: `git push origin feature/amazing-feature`
5. Open a pull request

Please make sure to update tests as appropriate and follow the code style guidelines.

## Future Enhancements

- **Additional Game Theory Models**: Implement Stackelberg, Bayesian, and other models
- **Advanced Machine Learning**: Integrate deep learning for prediction tasks
- **A/B Testing Framework**: Test different bidding strategies
- **Budget Pacing**: Advanced budget allocation and pacing algorithms

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For questions or feedback, please reach out to the project maintainers or open an issue in the GitHub repository. 