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

### Backend Setup

1. Ensure you have Java 11+ and Maven installed
2. Navigate to the `backend` directory
3. Run `mvn clean install` to build the project
4. Run `mvn spring-boot:run` to start the server

### Frontend Setup

1. Ensure you have Node.js 14+ and npm installed
2. Navigate to the `frontend` directory
3. Run `npm install` to install dependencies
4. Run `npm run dev` to start the development server
5. Open [http://localhost:3000](http://localhost:3000) in your browser

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

## Future Enhancements

- **Additional Game Theory Models**: Implement Stackelberg, Bayesian, and other models
- **Advanced Machine Learning**: Integrate deep learning for prediction tasks
- **A/B Testing Framework**: Test different bidding strategies
- **Budget Pacing**: Advanced budget allocation and pacing algorithms 